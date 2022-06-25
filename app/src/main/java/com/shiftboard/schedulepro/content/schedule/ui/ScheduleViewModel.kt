package com.shiftboard.schedulepro.content.schedule.ui

import androidx.lifecycle.*
import com.shiftboard.schedulepro.content.schedule.repo.ScheduleRepo
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.model.schedule.SummaryIconModel
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDate


class ScheduleViewModel(private val scheduleRepo: ScheduleRepo) : ViewModel() {
    private val _pageState: MutableStateFlow<SchedulePagingState> =
        MutableStateFlow(SchedulePagingState.NotInitialized)

    private val _calendarRangeState: MutableStateFlow<DateRangeState> =
        MutableStateFlow(DateRangeState.NotInitialized)

    private val _selectedDateState: MutableStateFlow<LocalDateState> =
        MutableStateFlow(LocalDateState.NotInitialized)


    val pageState: LiveData<SchedulePagingState> =
        _pageState.asLiveData(viewModelScope.coroutineContext)

    val pageData by lazy {
        scheduleRepo.allScheduleData()
            .asLiveData(viewModelScope.coroutineContext)
    }

    val syncCalendar: Boolean get() = _pageState.value is SchedulePagingState.Content

    private var supervisor: Job = SupervisorJob()
    private var supervisorScope = CoroutineScope(viewModelScope.coroutineContext + supervisor)

    private var lock: LocalDate? = null

    val selectedDateState: LiveData<Event<LocalDateSelect>> = _selectedDateState
        .filter { it is LocalDateState.Content }
        .map { (it as LocalDateState.Content).data }
        .asLiveData(viewModelScope.coroutineContext)

    val summaryData: LiveData<CacheResponse<HashMap<LocalDate, List<SummaryIconModel>>>> =
        _calendarRangeState
            .filter { it is DateRangeState.Content }
            .map { (it as DateRangeState.Content).data }
            .flatMapLatest { scheduleRepo.getScheduleSummary(it.start, it.end) }
            .asLiveData(viewModelScope.coroutineContext)

    fun invalidate(start: LocalDate, end: LocalDate) {
        viewModelScope.launch {
            scheduleRepo.cacheNewScheduleData(start, end)
        }
    }

    fun loadInitial(start: LocalDate, force: Boolean) {
        // If the content state is currently active we can just use that to load the current info
        if (start == lock) return

        if (_pageState.value is SchedulePagingState.Content) {
            val state = (_pageState.value as SchedulePagingState.Content)
            val selected = (_selectedDateState.value as? LocalDateState.Content)?.data?.peekContent()?.localDate ?: start

            val date = if (force) start else selected

            if (date >= state.startOffset && date <= state.endOffset) {
                setSelectedDate(date, true)
            } else {
                loadAtDate(date)
            }
        }
        // otherwise we need to (re)init the page using the date
        else {
            loadAtDate(start)
        }
    }

    fun loadAtDate(start: LocalDate) {
        if (start == lock) return
        lock = start

        supervisor.cancel()

        supervisor = SupervisorJob()
        supervisorScope = CoroutineScope(viewModelScope.coroutineContext + supervisor)

        val startDate = start.minusDays(DAYS_TO_LOAD)
        val endDate = start.plusDays(DAYS_TO_LOAD)

        supervisorScope.launch {
            _pageState.value = SchedulePagingState.Init(start)

            withContext(Dispatchers.IO) {
                scheduleRepo.deleteAllScheduleData()
                scheduleRepo.cacheNewScheduleData(startDate, endDate)
            }

            setSelectedDate(start, true)
            _pageState.value = SchedulePagingState.Content(start, startDate, endDate)
            lock = null
        }

    }

    /**
     * Can we jump to this date
     */
    fun isDateLoaded(date: LocalDate): Boolean {
        val state = (pageState.value as? SchedulePagingState.Content) ?: return false
        return date >= state.startOffset && date <= state.endOffset
    }

    /**
     * Can we get to this date via a normal load
     */
    fun isDateAdjacent(date: LocalDate): Boolean {
        val state = (pageState.value as? SchedulePagingState.Content) ?: return false

        val start = state.startOffset
        val adjStart = state.startOffset.minusDays(DAYS_TO_LOAD)

        val end = state.endOffset
        val adjEnd = state.endOffset.plusDays(DAYS_TO_LOAD)

        return date < start && date >= adjStart || date > end && date <= adjEnd
    }

    fun loadAdjacent(date: LocalDate) {
        val state = (pageState.value as? SchedulePagingState.Content) ?: return

        if (date > state.referenceDate) {
            loadMoreAtEnd()
        } else {
            loadMoreAtStart()
        }
    }

    fun loadMoreAtStart() {
        if (lock != null) return

        (pageState.value as? SchedulePagingState.Content)?.let { state ->
            supervisorScope.launch {
                try {
                    val start = state.startOffset.minusDays(DAYS_TO_LOAD)
                    val end = state.startOffset.minusDays(1)

                    scheduleRepo.cacheNewScheduleData(start, end)

                    _pageState.value = state.copy(startOffset = start)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun loadMoreAtEnd() {
        if (lock != null) return

        (pageState.value as? SchedulePagingState.Content)?.let { state ->
            supervisorScope.launch {
                try {
                    val start = state.endOffset.plusDays(1)
                    val end = state.endOffset.plusDays(DAYS_TO_LOAD)

                    scheduleRepo.cacheNewScheduleData(start, end)

                    _pageState.value = state.copy(endOffset = end)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun setCalendarRange(range: SummaryDateRange) {
        _calendarRangeState.value = DateRangeState.Content(range)
    }

    fun setSelectedDate(date: LocalDate, scrollRecycler: Boolean) {
        _selectedDateState.value = LocalDateState.Content(Event(LocalDateSelect(date, scrollRecycler)))
    }

    companion object {
        private const val DAYS_TO_LOAD = 45L
    }
}