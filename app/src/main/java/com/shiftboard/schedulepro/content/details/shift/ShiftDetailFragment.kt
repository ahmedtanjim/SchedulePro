package com.shiftboard.schedulepro.content.details.shift

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.root.SharedStateViewModel
import com.shiftboard.schedulepro.activities.root.SnackbarEvent
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.common.setComments
import com.shiftboard.schedulepro.common.setDetails
import com.shiftboard.schedulepro.content.details.repo.PermissionShiftDetails
import com.shiftboard.schedulepro.content.leaves.request.LeaveRequestDialog
import com.shiftboard.schedulepro.core.common.PageState
import com.shiftboard.schedulepro.core.common.analytics.LeaveRequestAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.analytics.UserActionAnalyticEvents
import com.shiftboard.schedulepro.core.common.base.BaseFragment
import com.shiftboard.schedulepro.core.common.setState
import com.shiftboard.schedulepro.core.common.utils.displayDate
import com.shiftboard.schedulepro.core.common.utils.observe
import com.shiftboard.schedulepro.databinding.ShiftDetailFragmentBinding
import com.shiftboard.schedulepro.ui.DetailsHeader
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import corm.shiftboard.schedulepro.content.details.shift.ShiftDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ShiftDetailFragment : BaseFragment() {
    override val titleRes: Int = R.string.shift_details
    override val layoutRes: Int = R.layout.shift_detail_fragment

    private val args by navArgs<ShiftDetailFragmentArgs>()
    private val sharedState by sharedViewModel<SharedStateViewModel>()

    private val boundView by lazyViewBinding<ShiftDetailFragmentBinding>()
    private val viewModel by viewModel<ShiftDetailViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.ShiftDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        lifecycleScope.launchWhenResumed {
            viewModel.shiftDetails.collectLatest {
                tryWith(boundView) {
                    when (it) {
                        is ShiftDetailState.ErrorState -> {
                            stateFlipper.setState(PageState.ERROR)
                            errorMessage.text = it.throwable?.message ?: getString(R.string.unknown_error)

                            errorButton.setText(R.string.ok)
                            errorButton.setOnClickListener {
                                finish()
                            }
                        }
                        is ShiftDetailState.LoadingState -> {
                            if (it.shiftDetails != null) {
                                stateFlipper.setState(PageState.SUCCESS)
                                loadShift(it.shiftDetails)
                            } else {
                                stateFlipper.setState(PageState.LOADING)
                            }
                        }
                        is ShiftDetailState.MainState -> {
                            stateFlipper.setState(PageState.SUCCESS)
                            loadShift(it.shiftDetails)
                        }
                    }
                }
            }
        }
        viewModel.turnDownState.observe(lifecycleScope) {
            if (it) finish()
        }
        viewModel.setShiftId(args.shiftId)
    }

    private fun loadShift(details: PermissionShiftDetails) {
        runBlocking {
            Log.d("onCreate", ": ${viewModel.tradePermission()}")
            if (!viewModel.tradePermission()){
                boundView.shiftDetails.trade.visibility = View.GONE
            }
        }
        tryWith(boundView) {
            val shift = details.shiftDetails
            // I'm not really worried about the hard-coding here because the design isn't finished
            shiftDetails.detailsHeader.setTitleText(shift.typeDescription)
            shiftDetails.detailsHeader.color = shift.color
            shiftDetails.detailsHeader.onNavigationClicked = DetailsHeader.OnNavigationClicked {
                finish()
            }

            shiftDetails.time.setText(
                TimeFormatters.spannedShortRange(shift.startTime, shift.startsOnNextDay||shift.startsOnPreviousDay,
                    shift.endTime, shift.endsOnNextDay||shift.endsOnPreviousDay, AppPrefs.militaryTime)
            )

            shiftDetails.comment.setComments(null, shift.comments, null)

            shiftDetails.date.setText(shift.date.displayDate())

            shiftDetails.location.setText(shift.locationDescription)
            shiftDetails.position.setText(shift.positionDescription)
            shiftDetails.hourSummary.setDetails(shift.hoursPaid, shift.regularOvertimeHours, shift.overtimeMultiplier, shift.color)

            shiftDetails.iconFlagContainer.removeAllViews()
            if (shift.regularOvertimeHours > 0f) {
                val icon = ImageView(requireContext())
                icon.setImageResource(R.drawable.overtime)
                icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.alert_red))
                icon.layoutParams = LinearLayout.LayoutParams(24.dp, 24.dp)
                shiftDetails.iconFlagContainer.addView(icon)

                shiftDetails.iconFlag.isVisible = true
            } else {
                shiftDetails.iconFlag.isVisible = false
            }
            shiftDetails.actionContainer.isVisible = false

            if (details.permissions.createLeave) {
                shiftDetails.actionContainer.isVisible = true
                shiftDetails.requestLeave.setOnClickListener {
                    analytics.logEvent(LeaveRequestAnalyticEvents.OnStart(false))
                    LeaveRequestDialog(shift.date) { start, end, message ->
                        sharedState.invalidateSchedule(start, end)
                        sharedState.postSnackbarNotification(SnackbarEvent(message, 500))
                        tryWithNav { navigateUp() }

                    }.show(childFragmentManager, REQUEST_TAG)
                }
            }
            if (shift.actions.contains(getString(R.string.submit_turndown_request_string))) {
                shiftDetails.actionContainer.isVisible = true

                shiftDetails.turndown.setOnClickListener {
                    analytics.logEvent(UserActionAnalyticEvents.Turndown)
                    viewModel.turndownShift()
                }
            }

            if (shift.actions.contains(getString(R.string.submit_turndown_cancel_string))) {
                shiftDetails.actionContainer.isVisible = true
                shiftDetails.turndown.text = getString(R.string.cancel_turndown)
                shiftDetails.turndown.setOnClickListener {
                    analytics.logEvent(UserActionAnalyticEvents.CancelTurndown)
                    viewModel.cancelTurndown()
                }
            }
            if (shift.actions.contains(getString(R.string.submit_trade_request_string))) {
                shiftDetails.actionContainer.isVisible = true
                shiftDetails.trade.text = getString(R.string.trade)
                shiftDetails.trade.setOnClickListener {
                    tryWithNav {
                        navigate(
                            ShiftDetailFragmentDirections.actionShiftDetailFragmentToTradeFragment("", shift.id, false)
                        )
                    }
                }
            }


            val set = ConstraintSet()
            set.clone(shiftDetails.root)
            set.applyTo(shiftDetails.root)

            shiftDetails.root.requestLayout()
        }
    }


    private fun finish() {
        tryWithNav {
            popBackStack()
        }
    }

    companion object {
        private const val REQUEST_TAG = "REQUEST_FRAGMENT_TAG"
    }
}