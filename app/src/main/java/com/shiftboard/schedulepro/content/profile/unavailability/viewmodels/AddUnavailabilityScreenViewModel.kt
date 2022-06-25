package com.shiftboard.schedulepro.content.profile.unavailability.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.content.profile.repo.ResourceProvider
import com.shiftboard.schedulepro.content.profile.repo.UnavailabilityRepo
import com.shiftboard.schedulepro.content.profile.unavailability.ScreenState
import com.shiftboard.schedulepro.content.profile.unavailability.components.CalenderDialogState
import com.shiftboard.schedulepro.content.profile.unavailability.components.TimeDialogState
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.profile.PatternType
import com.shiftboard.schedulepro.core.network.model.profile.Recurrence
import com.shiftboard.schedulepro.core.network.model.profile.Unavailability
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.*

class AddUnavailabilityScreenViewModel(
    private val unavailabilityRepo: UnavailabilityRepo,
    private val resourceProvider: ResourceProvider
) :
    ViewModel() {


    private val userId = CredentialPrefs.userId


    private val _calenderDialogState = MutableStateFlow(CalenderDialogState.Nothing)
    val calenderDialogState: StateFlow<CalenderDialogState> = _calenderDialogState

    fun changeCalenderDialogState(dialogState: CalenderDialogState) {
        _calenderDialogState.update { dialogState }
    }

    private val _startDate = MutableStateFlow<LocalDate>(LocalDate.now(Clock.systemDefaultZone()))
    val startDate: StateFlow<LocalDate> = _startDate

    fun changeStartDate(date: LocalDate) {
        _startDate.update { date }
    }

    private val _endDate = MutableStateFlow<LocalDate>(LocalDate.now(Clock.systemDefaultZone()))
    val endDate: StateFlow<LocalDate> = _endDate

    fun changeEndDate(date: LocalDate) {
        _endDate.update { date }
    }

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now(Clock.systemDefaultZone()))
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun changeSelectedDate(date: LocalDate) {
        _selectedDate.update { date }
    }

    private val _patternIntervalFocused = MutableStateFlow(false)
    val patternIntervalFocused: StateFlow<Boolean> = _patternIntervalFocused

    fun changePatternIntervalFocus(focus: Boolean) {
        _patternIntervalFocused.update { focus }
    }

    private val _sunday = MutableStateFlow(false)
    val sunday: StateFlow<Boolean> = _sunday

    fun changeSunDay() {
        _sunday.update { !sunday.value }
    }

    private val _monday = MutableStateFlow(false)
    val monday: StateFlow<Boolean> = _monday

    fun changeMonday() {
        _monday.update { !monday.value }
    }

    private val _tuesday = MutableStateFlow(false)
    val tuesday: StateFlow<Boolean> = _tuesday

    fun changeTuesday() {
        _tuesday.update { !tuesday.value }
    }

    private val _wednesday = MutableStateFlow(false)
    val wednesday: StateFlow<Boolean> = _wednesday

    fun changeWednesday() {
        _wednesday.update { !wednesday.value }
    }


    private val _thursday = MutableStateFlow(false)
    val thursday: StateFlow<Boolean> = _thursday

    fun changeThursday() {
        _thursday.update { !thursday.value }
    }


    private val _friday = MutableStateFlow(false)
    val friday: StateFlow<Boolean> = _friday

    fun changeFriday() {
        _friday.update { !friday.value }
    }


    private val _saturday = MutableStateFlow(false)
    val saturday: StateFlow<Boolean> = _saturday

    fun changeSaturday() {
        _saturday.update { !friday.value }
    }

    fun resetWeek() {
        _sunday.update { false }
        _monday.update { false }
        _tuesday.update { false }
        _wednesday.update { false }
        _thursday.update { false }
        _friday.update { false }
        _saturday.update { false }
    }


    private val _timeDialogState = MutableStateFlow<TimeDialogState>(TimeDialogState.Nothing)
    val timeDialogState: StateFlow<TimeDialogState> = _timeDialogState

    fun changeTimeDialog(timeDialog: TimeDialogState) {
        _timeDialogState.update { timeDialog }
    }

    private val _time = MutableStateFlow<LocalTime>(LocalTime.now(Clock.systemDefaultZone()))
    val time: StateFlow<LocalTime> = _time

    fun changeTime(time: LocalTime) {
        _time.update { time }
    }

    private val _startTime = MutableStateFlow<LocalTime>(LocalTime.of(9, 0))
    val startTime: StateFlow<LocalTime> = _startTime

    fun changeStartTime(time: LocalTime) {
        _startTime.update { time }
    }


    private val _endTime = MutableStateFlow<LocalTime>(LocalTime.of(17, 30))
    val endTime: StateFlow<LocalTime> = _endTime

    fun changeEndTime(time: LocalTime) {
        _endTime.update { time }
    }

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    fun changeIsSubmitting(isSubmitting: Boolean) {
        _isSubmitting.value = isSubmitting
    }


    private val _patternTypes = MutableStateFlow(
        listOf(
            PatternType.Daily,
            PatternType.Weekly,
            PatternType.Monthly,
            PatternType.Yearly
        )
    )
    val patternTypes: StateFlow<List<PatternType>> = _patternTypes

    private val _selectedPatternType = MutableStateFlow(PatternType.Daily)
    val selectedPatternType: StateFlow<PatternType> = _selectedPatternType

    fun changePatternType(type: PatternType) {
        _selectedPatternType.update { type }
    }


    private val _patternInterval = MutableStateFlow("1")
    val patternInterval: StateFlow<String> = _patternInterval

    fun changePatternInterval(interval: String) {
        _patternInterval.update { interval }
    }

    private val _month = MutableStateFlow("1")
    val month: StateFlow<String> = _month

    fun changeMonth(month: String) {
        _month.update { month }
    }

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject

    fun changeSubject(subject: String) {
        _subject.update { subject }
    }

    private val _allDaySwitchState = MutableStateFlow(false)
    val allDaySwitchState: StateFlow<Boolean> = _allDaySwitchState

    fun changeAllDaySwitchState(state: Boolean) {
        if (state) {
            _startTime.value = LocalTime.of(0, 0)
            _endTime.value = LocalTime.of(23, 0)
        } else {
            _startTime.value = LocalTime.of(9, 0)
            _endTime.value = LocalTime.of(17, 30)
        }
        _allDaySwitchState.update { state }
    }

    private val _recurrenceSwitchState = MutableStateFlow(false)
    val recurrenceSwitchState: StateFlow<Boolean> = _recurrenceSwitchState

    fun changeRecurrenceSwitchState(state: Boolean) {
        _recurrenceSwitchState.update { state }
    }

    private val _dayOfYear = MutableStateFlow("1")
    val dayOfYear: StateFlow<String> = _dayOfYear

    fun changeDayOfYear(dayOfYear: String) {
        _dayOfYear.update { dayOfYear }
    }

    private val _dayOfMonth = MutableStateFlow("1")
    val dayOfMonth: StateFlow<String> = _dayOfMonth

    fun changeDayOfMonth(day: String) {
        _dayOfMonth.update { day }
    }

    private val _months = MutableStateFlow(
        listOf(
            Month.JANUARY,
            Month.FEBRUARY,
            Month.MARCH,
            Month.APRIL,
            Month.MAY,
            Month.JUNE,
            Month.JULY,
            Month.MAY,
            Month.AUGUST,
            Month.SEPTEMBER,
            Month.OCTOBER,
            Month.NOVEMBER,
            Month.DECEMBER,
        )
    )
    val months: StateFlow<List<Month>> = _months

    private val _monthOfYear = MutableStateFlow(_months.value[0])
    val monthOfYear: StateFlow<Month> = _monthOfYear

    fun changeMonthOfYear(month: Month) {
        _monthOfYear.update { month }
    }


    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun submitUnavailability(
        id: String? = null,
        unavailabilityRecurrenceId: String? = null
    ) {
        viewModelScope.launch {
            _isSubmitting.update { true }
            delay(300)
            if (_subject.value.isEmpty()) {
                _eventFlow.emit(UIEvent.ShowToast(" Please add the subject "))
                _isSubmitting.update { false }

            } else if (_recurrenceSwitchState.value) {
                if (_startDate.value == _endDate.value) {
                    _eventFlow.emit(UIEvent.ShowToast(" Start and end date can't be same "))
                    _isSubmitting.update { false }
                } else {
                    val result = addRecurrences(
                        Recurrence(
                            id = id,
                            patternStartDate = _startDate.value.toString(),
                            patternEndDate = _endDate.value.toString(),
                            startTime = _startTime.value.toString(),
                            endTime = _endTime.value.toString(),
                            subject = _subject.value,
                            patternInterval = if (_selectedPatternType.value == PatternType.Yearly) _monthOfYear.value.value else if (_selectedPatternType.value == PatternType.Monthly) _month.value.toInt() else _patternInterval.value.toInt(),
                            patternDay = if (_selectedPatternType.value == PatternType.Yearly) _dayOfYear.value.toInt() else _dayOfMonth.value.toInt(),
                            patternType = _selectedPatternType.value.name,
                            daysOfWeekBitmask = "${_monday.value.toInt()}${_tuesday.value.toInt()}${_wednesday.value.toInt()}${_thursday.value.toInt()}${_friday.value.toInt()}${_saturday.value.toInt()}${_sunday.value.toInt()}".toInt(
                                2
                            ),
                            isWeekdaysOnly = true,
                            employeeId = userId
                        )
                    )
                    if (result) {
                        _eventFlow.emit(UIEvent.ChangeScreenState(ScreenState.UnavailabilityScreen))
                        changeSubject("")
                        changeIsSubmitting(false)
                    } else changeIsSubmitting(false)
                }
            } else if (!_recurrenceSwitchState.value) {
                val result = addUnavailability(
                    Unavailability(
                        id = id,
                        subject = _subject.value,
                        startTime = LocalDateTime.of(
                            _selectedDate.value.year,
                            _selectedDate.value.month,
                            _selectedDate.value.dayOfMonth,
                            if (_allDaySwitchState.value) 0 else _startTime.value.hour,
                            if (_allDaySwitchState.value) 0 else _startTime.value.minute
                        ).toString(),
                        endTime = LocalDateTime.of(
                            _selectedDate.value.year,
                            _selectedDate.value.month,
                            if (_allDaySwitchState.value)
                                _selectedDate.value.dayOfMonth + 1
                            else
                                _selectedDate.value.dayOfMonth,
                            if (_allDaySwitchState.value)
                                0
                            else
                                _endTime.value.hour,
                            if (_allDaySwitchState.value)
                                0
                            else
                                _endTime.value.minute
                        ).toString(),
                        isAllDay = _allDaySwitchState.value,
                        employeeId = userId,
                        unavailabilityRecurrenceId = unavailabilityRecurrenceId
                    )
                )
                if (result) {
                    _eventFlow.emit(UIEvent.ChangeScreenState(ScreenState.UnavailabilityScreen))
                    _isSubmitting.update { false }
                } else _isSubmitting.update { false }

            }
        }
    }

    private suspend fun addUnavailability(unavailability: Unavailability): Boolean {
        return when (val result =
            unavailabilityRepo.addUnavailabilities(unavailability = unavailability)) {
            is Maybe.Failure -> {
                result.exception?.message?.let {
                    _eventFlow.emit(UIEvent.ShowToast(it))
                }
                false
            }
            is Maybe.Success -> true
        }
    }

    private suspend fun addRecurrences(recurrence: Recurrence): Boolean {
        return when (val result = unavailabilityRepo.addRecurrences(recurrence)) {
            is Maybe.Failure -> {
                result.exception?.message?.let {
                    _eventFlow.emit(UIEvent.ShowToast(it))
                }
                false
            }
            is Maybe.Success -> true
        }
    }


    fun deleteUnavailability(unavailabilityId: String) {
        viewModelScope.launch {
            when (val result = unavailabilityRepo.deleteUnavailability(unavailabilityId)) {
                is Maybe.Failure -> {
                    result.exception?.message?.let {
                        changeIsSubmitting(false)
                        _eventFlow.emit(UIEvent.ShowToast(it))
                    }
                }
                is Maybe.Success -> {
                    changeIsSubmitting(false)
                    _eventFlow.emit(UIEvent.ChangeScreenState(ScreenState.UnavailabilityScreen))
                }
            }

        }
    }


    fun deleteRecurrence(unavailabilityRecurrenceId: String) {
        viewModelScope.launch {
            when (val result = unavailabilityRepo.deleteRecurrence(unavailabilityRecurrenceId)) {
                is Maybe.Failure -> {
                    result.exception?.message?.let {
                        changeIsSubmitting(false)
                        _eventFlow.emit(UIEvent.ShowToast(it))
                    }
                }
                is Maybe.Success -> {
                    changeIsSubmitting(false)
                    _eventFlow.emit(UIEvent.ChangeScreenState(ScreenState.UnavailabilityScreen))
                }
            }

        }
    }


    sealed class UIEvent {
        data class ShowToast(val message: String) : UIEvent()
        data class ChangeScreenState(val state: ScreenState) : UIEvent()
    }

    private fun Boolean.toInt() = if (this) 1 else 0


}