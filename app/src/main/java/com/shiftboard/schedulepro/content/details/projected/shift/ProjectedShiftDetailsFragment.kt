package com.shiftboard.schedulepro.content.details.projected.shift

import android.content.res.ColorStateList
import android.os.Bundle
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
import com.shiftboard.schedulepro.content.leaves.request.LeaveRequestDialog
import com.shiftboard.schedulepro.core.common.PageState
import com.shiftboard.schedulepro.core.common.analytics.LeaveRequestAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.base.BaseFragment
import com.shiftboard.schedulepro.core.common.setState
import com.shiftboard.schedulepro.core.common.utils.displayDate
import com.shiftboard.schedulepro.core.common.utils.parseDateArg
import com.shiftboard.schedulepro.core.persistence.model.details.ProjectedShiftDetails
import com.shiftboard.schedulepro.databinding.ShiftDetailFragmentBinding
import com.shiftboard.schedulepro.resources.ScheduleListActions
import com.shiftboard.schedulepro.ui.DetailsHeader
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProjectedShiftDetailsFragment : BaseFragment() {
    override val titleRes: Int = R.string.shift_details
    override val layoutRes: Int = R.layout.shift_detail_fragment

    private val args by navArgs<ProjectedShiftDetailsFragmentArgs>()
    private val sharedState by sharedViewModel<SharedStateViewModel>()

    private val boundView by lazyViewBinding<ShiftDetailFragmentBinding>()
    private val viewModel by viewModel<ProjectedShiftDetailViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.ProjectedShiftDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.shiftDetails.collectLatest {
                tryWith(boundView) {
                    when (it) {
                        is ProjectedShiftDetailState.ErrorState -> {
                            stateFlipper.setState(PageState.ERROR)
                            errorMessage.text = it.throwable?.message ?: getString(R.string.unknown_error)

                            errorButton.setText(R.string.ok)
                            errorButton.setOnClickListener {
                                finish()
                            }
                        }
                        is ProjectedShiftDetailState.LoadingState -> {
                            if (it.shiftDetails != null) {
                                stateFlipper.setState(PageState.SUCCESS)
                                loadShift(it.shiftDetails)
                            } else {
                                stateFlipper.setState(PageState.LOADING)
                            }
                        }
                        is ProjectedShiftDetailState.MainState -> {
                            stateFlipper.setState(PageState.SUCCESS)
                            loadShift(it.shiftDetails)
                        }
                        else -> {}
                    }
                }
            }
        }

        viewModel.setShiftId(args.date.parseDateArg(), args.shitId)
    }

    private fun loadShift(details: ProjectedShiftDetails) {
        tryWith(boundView) {
            // I'm not really worried about the hard-coding here because the design isn't finished
            shiftDetails.detailsHeader.setTitleText(getString(R.string.projected_title, details.shiftTimeDescription))
            shiftDetails.detailsHeader.color = details.color
            shiftDetails.detailsHeader.dashed = true
            shiftDetails.detailsHeader.onNavigationClicked = DetailsHeader.OnNavigationClicked {
                finish()
            }

            shiftDetails.time.setText(
                TimeFormatters.spannedShortRange(details.startTime, details.startsOnNextDay||details.startsOnPreviousDay,
                    details.endTime, details.endsOnNextDay||details.endsOnPreviousDay, AppPrefs.militaryTime)
            )

            // No comments on projected shifts
            shiftDetails.comment.setComments(null, null, null)

            shiftDetails.date.setText(details.date.displayDate())

            shiftDetails.location.setText(details.locationDescription)
            shiftDetails.position.setText(details.positionDescription)
            shiftDetails.hourSummary.setDetails(details.hoursPaid, details.regularOvertimeHours, details.overtimeMultiplier, details.color)

            shiftDetails.iconFlagContainer.removeAllViews()
            if (details.regularOvertimeHours > 0f) {
                val icon = ImageView(requireContext())
                icon.setImageResource(R.drawable.overtime)
                icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.alert_red))
                icon.layoutParams = LinearLayout.LayoutParams(24.dp, 24.dp)
                shiftDetails.iconFlagContainer.addView(icon)

                shiftDetails.iconFlag.isVisible = true
            } else {
                shiftDetails.iconFlag.isVisible = false
            }

            if (ScheduleListActions.SUBMIT_LEAVE in details.actions) {
                shiftDetails.actionContainer.isVisible = true
                shiftDetails.requestLeave.setOnClickListener {
                    analytics.logEvent(LeaveRequestAnalyticEvents.OnStart(false))
                    LeaveRequestDialog(details.date) { start, end, message ->
                        sharedState.invalidateSchedule(start, end)
                        sharedState.postSnackbarNotification(SnackbarEvent(message, 500))
                        tryWithNav { navigateUp() }

                    }.show(childFragmentManager, REQUEST_TAG)
                }
            } else {
                shiftDetails.actionContainer.isVisible = false
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