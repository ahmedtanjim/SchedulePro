package com.shiftboard.schedulepro.content.details.leave

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
import com.shiftboard.schedulepro.core.persistence.model.details.Leave
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveDetails
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveRequestDetails
import com.shiftboard.schedulepro.databinding.LeaveDetailFragmentBinding
import com.shiftboard.schedulepro.ui.DetailsHeader
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class LeaveDetailFragment : BaseFragment() {
    override val titleRes: Int = R.string.leave_details
    override val layoutRes: Int = R.layout.leave_detail_fragment

    private val args by navArgs<LeaveDetailFragmentArgs>()

    private val boundView by lazyViewBinding<LeaveDetailFragmentBinding>()
    private val viewModel by viewModel<LeaveDetailViewModel>()

    private var loadingDialog: MaterialDialog? = null

    private val sharedState by sharedViewModel<SharedStateViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.LeaveDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.leaveDetailState.collectLatest {
                tryWith(boundView) {
                    when (it) {
                        is LeaveDetailState.ErrorState -> {
                            stateFlipper.displayedChild = 1
                            errorMessage.text = it.throwable?.message ?: getString(R.string.unknown_error)
                        }
                        is LeaveDetailState.LoadingState -> {
                            if (it.leaveDetails != null) {
                                loadLeave(it.leaveDetails)
                            } else {
                                stateFlipper.displayedChild = 0
                            }
                        }
                        is LeaveDetailState.MainState -> {
                            loadLeave(it.leaveDetails)
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
                                    lifecycleOwner(this@LeaveDetailFragment)
                                    cancelOnTouchOutside(false)
                                    cancelable(false)
                                }
                                loadingDialog?.show()
                            } else {
                                loadingDialog?.cancel()
                            }
                        }
                        is LeaveDetailState.DeleteSuccessState -> {
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

        viewModel.setLeaveId(args.leaveId)
    }

    private fun finish() {
        tryWithNav {
            popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tryWith(boundView) {
            leaveDetails.requestHourSummary.root.isVisible = false
            closeIcon.setOnClickListener {
                finish()
            }
            retry.setOnClickListener {
                viewModel.setLeaveId(args.leaveId)
            }
        }
    }

    private fun loadLeave(leave: Leave) {
        tryWith(boundView) {
            loadLeaveDetails(leave.leave)
            leave.leaveRequest?.let { loadLeaveRequest(it) }

            stateFlipper.displayedChild = 2
        }
    }

    private fun loadLeaveDetails(details: LeaveDetails) {
        tryWith(boundView) {
            with(leaveDetails) {
                detailsHeader.onNavigationClicked = DetailsHeader.OnNavigationClicked {
                    finish()
                }
                detailsHeader.setTitleText(details.typeDescription)
                detailsHeader.color = details.color

                hourSummary.setDetails(details.hoursPaid, details.regularOvertimeHours, details.overtimeMultiplier, details.color)

                time.setText(TimeFormatters.spannedRange(time.context, details.startTime, details.endTime, AppPrefs.militaryTime))

                comment.setComments(null, details.comments, null)

                date.setText(details.date.displayDate())

                location.setText(details.locationDescription)
                position.setText(details.positionDescription)

                iconFlagContainer.removeAllViews()
                val icon = ImageView(requireContext())
                icon.setImageResource(R.drawable.ic_time_off)
                icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black))
                icon.layoutParams = LinearLayout.LayoutParams(24.dp, 24.dp)
                iconFlagContainer.addView(icon)
                iconFlag.isVisible = true

            }
        }
    }

    private fun loadLeaveRequest(requestDetails: LeaveRequestDetails) {
        tryWith(boundView) {
            with(leaveDetails) {
                leaveRequestHeader.isVisible = true
                leaveRequestHeader.setText(requestDetails.leaveTypeDescription)
                leaveRequestHeader.color = requestDetails.color

                if (requestDetails.startDate.isSameDay(requestDetails.endDate)) {
                    requestDate.setText(requestDetails.startDate.displayDate())
                } else {
                    requestDate.setText(displayDateRange(requestDetails.startDate, requestDetails.endDate))
                }
                requestDate.isVisible = true

                if (requestDetails.startTime != null && requestDetails.endTime != null) {
                    requestTime.isVisible = true
                    requestTime.setText(TimeFormatters.spannedRange(requestTime.context, requestDetails.startTime!!,
                        requestDetails.endTime!!, AppPrefs.militaryTime))
                } else {
                    requestTime.isVisible = false
                }
                requestHourSummary.setDetails(requestDetails.requestedTimeOff, 0f, 0f, requestDetails.color)
                requestHourSummary.root.isVisible = true

                status.setText(statusString(requestDetails.leaveRequestStatus))
                status.isVisible = true

                requestComment.setComments(
                    requestDetails.managerComments,
                    requestDetails.requesterComments,
                    requestDetails.amendmentRequesterComments
                )

                if (requestDetails.actions.contains("RequestCancellation")) {
                    requestCancellation.isVisible = true
                    requestCancellation.setOnClickListener {
                        viewModel.requestCancellation(requestDetails.id)
                    }
                } else {
                    requestCancellation.isVisible = false
                }

                if (requestDetails.actions.contains("Delete")) {
                    delete.isVisible = true
                    delete.setOnClickListener {
                        viewModel.requestDelete(requestDetails.id)
                    }
                } else {
                    delete.isVisible = false
                }
            }
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