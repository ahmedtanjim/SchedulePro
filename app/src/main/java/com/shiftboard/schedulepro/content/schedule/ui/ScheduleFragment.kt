@file:Suppress("ClickableViewAccessibility")

package com.shiftboard.schedulepro.content.schedule.ui

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.root.BottomNavFragment
import com.shiftboard.schedulepro.activities.root.SnackbarEvent
import com.shiftboard.schedulepro.content.leaves.request.LeaveRequestDialog
import com.shiftboard.schedulepro.content.notifications.messages.TradeDetailsFragment
import com.shiftboard.schedulepro.content.notifications.ui.NotificationsFragmentArgs
import com.shiftboard.schedulepro.content.openShifts.OpenShiftRequestDialog
import com.shiftboard.schedulepro.content.overtime.SignupDialogFragment
import com.shiftboard.schedulepro.core.common.analytics.LeaveRequestAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.analytics.SignupAnalyticEvents
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import com.shiftboard.schedulepro.databinding.ScheduleFragmentBinding
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.schedule.ScheduleWidget
import com.shiftboard.schedulepro.ui.tryWith
import com.shiftboard.schedulepro.ui.utils.disableAnimations
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber


class ScheduleFragment : BottomNavFragment() {
    override val titleRes: Int = R.string.schedule
    override val layoutRes: Int = R.layout.schedule_fragment

    private val viewModel by sharedViewModel<ScheduleViewModel>()


    private val boundView by lazyViewBinding<ScheduleFragmentBinding>()
    private val touchDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                toggleMonthMode()
                return super.onSingleTapUp(e)
            }
        })
    }

    private val adapter by lazy {
        ScheduleListAdapter(object : ScheduleListAdapter.ElementClickListener {
            override fun onProjectedShiftElementClickLister(element: ProjectedShiftElement) {
                tryWithNav {
                    navigate(
                        ScheduleFragmentDirections.actionScheduleToProjectedShiftDetailsFragment(
                            element.shiftTimeId, element.date.format(DateTimeFormatter.ISO_DATE)
                        )
                    )
                }
            }

            override fun onProjectedLeaveElementClickLister(element: ProjectedLeaveElement) {
                tryWithNav {
                    navigate(
                        ScheduleFragmentDirections.actionScheduleToProjectedLeaveDetailFragment(
                            element.leaveTypeId, element.date.format(DateTimeFormatter.ISO_DATE)
                        )
                    )
                }
            }

            override fun onShiftElementClickLister(element: ShiftElement) {
                tryWithNav {
                    navigate(
                        ScheduleFragmentDirections.actionScheduleToShiftDetailFragment(element.id)
                    )
                }
            }

            override fun onLeaveElementClickLister(element: LeaveElement) {
                tryWithNav {
                    navigate(
                        ScheduleFragmentDirections.actionScheduleToLeaveDetailFragment(element.id)
                    )
                }
            }

            override fun onPendingElementClickLister(element: PendingLeaveElement) {
                tryWithNav {
                    navigate(
                        ScheduleFragmentDirections.actionScheduleToRequestDetailFragment(element.id)
                    )
                }
            }

            override fun onOpenShiftElementClickListener(element: OpenShiftEvent) {
                OpenShiftRequestDialog(element.date) { start, end, message ->
                    sharedState.invalidateSchedule(start, end)
                    sharedState.postSnackbarNotification(SnackbarEvent(message))
                }.show(childFragmentManager, REQUEST_TAG)
            }

            override fun onCreateLeaveClickLister(element: LocalDate) {
                analytics.logEvent(LeaveRequestAnalyticEvents.OnStart(true))
                LeaveRequestDialog(element) { start, end, message ->
                    sharedState.invalidateSchedule(start, end)
                    sharedState.postSnackbarNotification(SnackbarEvent(message))
                }.show(childFragmentManager, REQUEST_TAG)
            }

            override fun onCreateSignupClickLister(element: LocalDate) {
                analytics.logEvent(SignupAnalyticEvents.OnStart(true))
                SignupDialogFragment(element) { date, message ->
                    sharedState.invalidateSchedule(date, date)
                    sharedState.postSnackbarNotification(SnackbarEvent(message))
                }.show(childFragmentManager, REQUEST_TAG)
            }

            override fun onCreateBidClickListener(element: LocalDate) {
                OpenShiftRequestDialog(element) { start, end, message ->
                    sharedState.invalidateSchedule(start, end)
                    sharedState.postSnackbarNotification(SnackbarEvent(message))
                }.show(childFragmentManager, REQUEST_TAG)
            }

            override fun onTradeElementClickListener(element: TradeEvent) {
                TradeDetailsFragment(element.id) { start, end, message ->
                    if (start < end) {
                        sharedState.invalidateSchedule(start, end)
                    } else {
                        sharedState.invalidateSchedule(end, start)
                    }
                    sharedState.postSnackbarNotification(SnackbarEvent(message))
                }.show(childFragmentManager, REQUEST_TAG)
            }
        })
    }

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.Schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryWith(boundView) {

            sharedState.invalidateScheduleTracker.observe(viewLifecycleOwner) {
                it.doUnlessHandledOrNull { event ->
                    viewModel.invalidate(
                        event.startDate,
                        event.endDate
                    )
                }
            }

            viewModel.selectedDateState.observe(viewLifecycleOwner) { event -> attemptToScroll(event) }

            viewModel.pageData.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                if (it.count() in 1..93)
                    viewModel.setCalendarRange(SummaryDateRange(it.first().date, it.last().date))
            }

            scheduleContent.constraintLayout.setOnTouchListener { v, event ->
                touchDetector.onTouchEvent(event)
                v.onTouchEvent(event)
            }

            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)

                    val date = viewModel.selectedDateState.value?.peekContent()?.localDate ?: return

                    val position = adapter.tryToFindDate(date)
                    if (position >= 0) {
                        stateFlipper.displayedChild = 2
                        viewModel.setSelectedDate(date, true)
                        viewModel.setCalendarRange(
                            SummaryDateRange(
                                date,
                                date.plusDays(itemCount.toLong())
                            )
                        )
                    }
                }
            })

            viewModel.pageState.observe(viewLifecycleOwner) {
                when (it) {
                    is SchedulePagingState.Init -> {
                        stateFlipper.displayedChild = 0
                    }
                    is SchedulePagingState.Error -> {
                        stateFlipper.displayedChild = 1
                    }
                    is SchedulePagingState.Content -> {
                        stateFlipper.displayedChild = 2
                    }
                    else -> {
                        stateFlipper.displayedChild = 0
                    }
                }
            }

            viewModel.summaryData.observe(viewLifecycleOwner) {
                when (it) {
                    is CacheResponse.Success -> {
                        scheduleContent.calendarWidget.setIconHash(it.data)
                    }
                    is CacheResponse.Failure -> {
                    }
                    else -> {}
                }
            }

            scheduleContent.scheduleRecycler.layoutManager = LinearLayoutManager(requireContext())
            scheduleContent.scheduleRecycler.adapter = adapter

            scheduleContent.scheduleRecycler.disableAnimations()

            scheduleContent.calendarWidget.visibleDateRangeListener =
                ScheduleWidget.OnVisibleRangeChangeListener { startDate, endDate ->
                    viewModel.setCalendarRange(SummaryDateRange(startDate, endDate))
                }

            scheduleContent.calendarWidget.dateSelectedListener =
                ScheduleWidget.OnDateSelectedListener {
                    viewModel.setSelectedDate(it, true)
                }

            scheduleContent.scheduleRecycler.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy < 0) {
                        val position =
                            (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                                ?: 0
                        if (viewModel.syncCalendar) {
                            adapter.getDateForPosition(position)?.let {
                                adapter.getDateForPosition(position)?.let {
                                    viewModel.setSelectedDate(it, false)
                                }
                            }
                        }
                        if (position < 15) {
                            viewModel.loadMoreAtStart()
                        }

                    } else {
                        val position =
                            (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition()
                                ?: 0
                        if (viewModel.syncCalendar) {
                            adapter.getDateForPosition(position)?.let {
                                viewModel.setSelectedDate(it, false)
                            }
                        }
                        val endPosition =
                            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition()
                                ?: 0
                        if (adapter.itemCount - endPosition < 15) {
                            viewModel.loadMoreAtEnd()
                        }
                    }
                }
            })
            val args by navArgs<ScheduleFragmentArgs>()
            if (arguments == null){
                arguments = Bundle()
            }

            Timber.d("::: Args Date ${args.startDate}")
            val startDate = args.startDate?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) } ?: LocalDate.now()
            if (startDate.year > 2050 || startDate.year < 2000) {
                viewModel.loadInitial(LocalDate.now(), args.startDate != null)
            } else viewModel.loadInitial(startDate, args.startDate != null)
        }
    }

    private fun toggleMonthMode() {
        tryWith(boundView) {
            if (scheduleContent.constraintLayout.currentState == R.id.month) {
                scheduleContent.constraintLayout.transitionToStart()
            } else {
                scheduleContent.constraintLayout.transitionToEnd()
            }
        }
    }

    private fun collapseMonthMode() {
        tryWith(boundView) {
            scheduleContent.constraintLayout.transitionToStart()
        }
    }

    private fun attemptToScroll(event: Event<LocalDateSelect>) {
        tryWith(boundView) {
            val date = event.peekContent()
            when {
                // we can jump to the date we selected
                date.scrollRecycler && viewModel.isDateLoaded(date.localDate) -> {
                    val position = adapter.tryToFindDate(date.localDate)

                    if (position < 0) return
                    event.doUnlessHandledOrNull {
                        (scheduleContent.scheduleRecycler.layoutManager as LinearLayoutManager)
                            .scrollToPositionWithOffset(position, 0)

                        scheduleContent.calendarWidget.setSelectedDate(date.localDate)
                        collapseMonthMode()
                    }

                }
                // The selected date is in the adjacent data set
                date.scrollRecycler && viewModel.isDateAdjacent(date.localDate) -> {
                    viewModel.loadAdjacent(date.localDate)
                }
                // We need to do a jump
                date.scrollRecycler -> {
                    viewModel.loadAtDate(date.localDate)
                }
                else -> {
                    event.doUnlessHandledOrNull {
                        scheduleContent.calendarWidget.setSelectedDate(date.localDate)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_TAG = "REQUEST_FRAGMENT_TAG"
    }
}