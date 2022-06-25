package com.shiftboard.schedulepro.content.details.projected.leave

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.common.setComments
import com.shiftboard.schedulepro.common.setDetails
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.base.BaseFragment
import com.shiftboard.schedulepro.core.common.utils.*
import com.shiftboard.schedulepro.core.persistence.model.details.ProjectedLeaveDetails
import com.shiftboard.schedulepro.databinding.LeaveDetailFragmentBinding
import com.shiftboard.schedulepro.ui.DetailsHeader
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.formatting.TimeFormatters
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProjectedLeaveDetailFragment : BaseFragment() {
    override val titleRes: Int = R.string.leave_details
    override val layoutRes: Int = R.layout.leave_detail_fragment

    private val args by navArgs<ProjectedLeaveDetailFragmentArgs>()

    private val boundView by lazyViewBinding<LeaveDetailFragmentBinding>()
    private val viewModel by viewModel<ProjectedLeaveDetailViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.ProjectedLeaveDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.leaveDetailState.collectLatest {
                tryWith(boundView) {
                    when (it) {
                        is ProjectedLeaveDetailState.ErrorState -> {
                            stateFlipper.displayedChild = 1
                            errorMessage.text = it.throwable?.message ?: getString(R.string.unknown_error)
                        }
                        is ProjectedLeaveDetailState.LoadingState -> {
                            if (it.leaveDetails != null) {
                                loadLeave(it.leaveDetails)
                            } else {
                                stateFlipper.displayedChild = 0
                            }
                        }
                        is ProjectedLeaveDetailState.MainState -> {
                            loadLeave(it.leaveDetails)
                        }
                        else -> {}
                    }
                }
            }
        }

        viewModel.setLeaveId(args.date.parseDateArg(), args.leaveId)
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
                viewModel.setLeaveId(args.date.parseDateArg(), args.leaveId)
            }
        }
    }

    private fun loadLeave(leave: ProjectedLeaveDetails) {
        tryWith(boundView) {
            with(leaveDetails) {
                detailsHeader.onNavigationClicked = DetailsHeader.OnNavigationClicked {
                    finish()
                }
                detailsHeader.setTitleText(getString(R.string.projected_title, leave.leaveTypeDescription))
                detailsHeader.color = leave.color
                detailsHeader.dashed = true
                detailsHeader.drawBorder = true

                hourSummary.setDetails(leave.hoursPaid, leave.regularOvertimeHours, leave.overtimeMultiplier, leave.color)

                time.setText(TimeFormatters.spannedRange(time.context, leave.startTime, leave.endTime, AppPrefs.militaryTime))

                comment.setComments(null, null, null)

                date.setText(leave.date.displayDate())

                location.setText(leave.locationDescription)
                position.setText(leave.positionDescription)

                iconFlagContainer.removeAllViews()
                val icon = ImageView(requireContext())
                icon.setImageResource(R.drawable.ic_time_off)
                icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.black))
                icon.layoutParams = LinearLayout.LayoutParams(24.dp, 24.dp)
                iconFlagContainer.addView(icon)
                iconFlag.isVisible = true

            }

            stateFlipper.displayedChild = 2
        }
    }
}