package com.shiftboard.schedulepro.content.details.request

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.root.SharedStateViewModel
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.common.setComments
import com.shiftboard.schedulepro.common.setDetails
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.base.BaseFragment
import com.shiftboard.schedulepro.core.common.utils.*
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveRequestDetails
import com.shiftboard.schedulepro.databinding.RequestDetailFragmentBinding
import com.shiftboard.schedulepro.ui.DetailsHeader
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RequestDetailFragment : BaseFragment() {
    override val titleRes: Int = R.string.leave_request
    override val layoutRes: Int = R.layout.request_detail_fragment

    private val args by navArgs<RequestDetailFragmentArgs>()

    private val boundView by lazyViewBinding<RequestDetailFragmentBinding>()
    private val viewModel by viewModel<RequestDetailViewModel>()
    private val sharedState by sharedViewModel<SharedStateViewModel>()

    private var loadingDialog: MaterialDialog? = null

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.RequestDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.requestDetailState.collectLatest {
                tryWith(boundView) {
                    when (it) {
                        is RequestDetailState.ErrorState -> {
                            stateFlipper.displayedChild = 1
                            tryWith(boundView) {
                                errorMessage.text = it.throwable?.message ?: getString(R.string.unknown_error)
                            }
                        }
                        is RequestDetailState.LoadingState -> {
                            if (it.requestDetails != null) {
                                loadLeave(it.requestDetails)
                            } else {
                                stateFlipper.displayedChild = 0
                            }
                        }
                        is RequestDetailState.MainState -> {
                            loadLeave(it.requestDetails)
                            it.messageEvent?.doUnlessHandledOrNull {
                                showMaterialDialog {
                                    message(text = it)
                                    positiveButton(R.string.ok)
                                }
                            }
                            it.cancelSuccess?.doUnlessHandledOrNull { event ->
                                sharedState.invalidateSchedule(event.startDate, event.endDate)
                            }
                            if (it.runningCancel || it.runningDelete) {
                                loadingDialog?.cancel()
                                loadingDialog = buildMaterialDialog {
                                    customView(R.layout.loading_dialog)
                                    lifecycleOwner(this@RequestDetailFragment)
                                    cancelOnTouchOutside(false)
                                    cancelable(false)
                                }
                                loadingDialog?.show()
                            } else {
                                loadingDialog?.cancel()
                            }
                        }
                        is RequestDetailState.DeleteSuccessState -> {
                            sharedState.invalidateSchedule(it.invalidateStart, it.invalidateEnd)

                            showMaterialDialog {
                                message(R.string.request_delete_success)
                                positiveButton(R.string.ok) {
                                    finish()
                                }
                                onDismiss {
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }

        viewModel.setRequestId(args.requestId)
    }

    private fun finish() {
        tryWithNav {
            popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryWith(boundView) {
            closeIcon.setOnClickListener {
                finish()
            }
            retry.setOnClickListener {
                viewModel.setRequestId(args.requestId)
            }
        }
    }

    private fun loadLeave(requestDetails: LeaveRequestDetails) {
        tryWith(boundView) {
            details.detailsHeader.setTitleText(requestDetails.leaveTypeDescription)
            details.detailsHeader.onNavigationClicked = DetailsHeader.OnNavigationClicked {
                finish()
            }

            when (requestDetails.status) {
                "Accepted", "Accepted_PendingEdit", "Accepted_CancellationRequested" -> {
                    details.detailsHeader.setOverrideColor(ContextCompat.getColor(requireContext(), R.color.approved_leave), true)
                }
                else -> {
                    details.detailsHeader.setOverrideColor(ContextCompat.getColor(requireContext(), R.color.very_light_gray), false)
                }
            }

            details.detailsHeader.color = requestDetails.color

            if (requestDetails.startDate.isSameDay(requestDetails.endDate)) {
                details.date.setText(requestDetails.startDate.displayDate())
            } else {
                details.date.setText(displayDateRange(requestDetails.startDate, requestDetails.endDate))
            }

            if (requestDetails.startTime != null && requestDetails.endTime != null) {
                details.time.isVisible = true
                details.time.setText(TimeFormatters.spannedRange(details.time.context, requestDetails.startTime!!,
                    requestDetails.endTime!!, AppPrefs.militaryTime))
            } else {
                details.time.isVisible = true
                details.time.setText(getString(R.string.all_day))
            }

            details.hourSummary.setDetails(requestDetails.requestedTimeOff, 0f, 0f, requestDetails.color)
            details.status.setText(statusString(requestDetails.leaveRequestStatus))

            details.comment.setComments(requestDetails.managerComments, requestDetails.requesterComments, requestDetails.amendmentRequesterComments)

            details.iconFlagContainer.removeAllViews()
            val icon = ImageView(requireContext())
            icon.setImageResource(R.drawable.ic_time_off)
            icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black))
            icon.layoutParams = LinearLayout.LayoutParams(24.dp, 24.dp)
            details.iconFlagContainer.addView(icon)
            details.iconFlag.isVisible = true

            if (requestDetails.actions.contains("RequestCancellation")) {
                details.requestCancellation.isVisible = true
                details.requestCancellation.setOnClickListener {
                    viewModel.requestCancellation(requestDetails.id)
                }
            } else {
                details.requestCancellation.isVisible = false
            }

            if (requestDetails.actions.contains("Delete")) {
                details.delete.isVisible = true
                details.delete.setOnClickListener {
                    viewModel.requestDelete(requestDetails.id)
                }
            } else {
                details.delete.isVisible = false
            }

            stateFlipper.displayedChild = 2
        }
    }

    private fun statusString(status: String): String {
        return when (status) {
            "Pending" -> getString(R.string.leave_request_status_pending)
            "Accepted" -> getString(R.string.leave_request_status_accepted)
            "Accepted_PendingEdit" -> getString(R.string.leave_request_status_accepted_pending_edit)
            "Accepted_CancellationRequested" -> getString(R.string.leave_request_status_accepted_cancellation_requested)
            "Declined" -> getString(R.string.leave_request_status_declined)
            "RevokedAfterApproval" -> getString(R.string.leave_request_status_revoked)
            "CancelledAfterApproval" -> getString(R.string.leave_request_status_cancelled)
            else -> ""
        }
    }
}