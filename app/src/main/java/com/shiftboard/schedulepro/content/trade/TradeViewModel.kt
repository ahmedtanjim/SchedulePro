package com.shiftboard.schedulepro.content.trade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.common.inject.providers.FirebaseAnalyticsProvider
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.content.group.domain.GroupsRepo
import com.shiftboard.schedulepro.content.profile.repo.ProfileRepo
import com.shiftboard.schedulepro.content.trade.domain.repository.TradesRepo
import com.shiftboard.schedulepro.content.trade.screens.main.AlertState
import com.shiftboard.schedulepro.content.trade.screens.main.TradeScreenState
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.TradeRequestAnalyticEvents
import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.TradeDetailsElement
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.details.ShiftDetailsModel
import com.shiftboard.schedulepro.core.network.model.group.*
import com.shiftboard.schedulepro.core.network.model.trade.TradeRequest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber


class TradeViewModel(private val tradesRepo: TradesRepo, private val groupsRepo: GroupsRepo, private val profileRepo: ProfileRepo) :
    ViewModel() {

    private val _screenState = MutableStateFlow(TradeScreenState.Loading)
    private val _alertState = MutableStateFlow(AlertState.NONE)
    private val _filteredEmployees = MutableStateFlow<List<Employee>>(listOf())
    private val _shifts = MutableStateFlow<List<TradeShift>>(listOf())
    private val _unfilteredEmployees = MutableStateFlow<List<Employee>>(listOf())
    private val _filters = MutableStateFlow(GroupFiltersResponse())
    private val _schedule = MutableStateFlow<List<GroupScheduleItemCollection>>(listOf())
    private val pageSize = 14L
    private var analytics : FirebaseAnalyticsProvider? = null

    val userDetailsLiveDate by lazy {
        profileRepo.userDetails().asLiveData(viewModelScope.coroutineContext)
    }

    private val _progressBarState = MutableStateFlow(false)
    val progressBarState: StateFlow<Boolean> = _progressBarState


    private val _selectedEmployeeShifts = MutableStateFlow<MutableList<TradeShift>>(mutableListOf())
    val selectedEmployeeShifts: StateFlow<List<TradeShift>> = _selectedEmployeeShifts

    private val _selectedEmployeeShiftsSize = MutableStateFlow(0)
    val selectedEmployeeShiftsSize: StateFlow<Int> = _selectedEmployeeShiftsSize

    fun addOrRemoveEmployeeSchedule(selected: Boolean, schedule: TradeShift) {
        if (selected) {
            _selectedEmployeeShifts.value.add(schedule)
            _selectedEmployeeShiftsSize.value++
        } else {
            _selectedEmployeeShifts.value.remove(
                schedule
            )
            _selectedEmployeeShiftsSize.value--
        }
    }

    fun changeSelectedShift(shift: TradeShift){
        _selectedEmployeeShifts.value.clear()
        _selectedEmployeeShifts.value.add(shift)
    }

    private val _ownScheduleLoaded = MutableStateFlow(false)
    val ownScheduleLoaded: StateFlow<Boolean> = _ownScheduleLoaded

    private val _ownSchedules = MutableStateFlow<GroupScheduleItemCollection?>(null)
    val ownSchedules: StateFlow<GroupScheduleItemCollection?> = _ownSchedules


    private val  _selectedOwnSchedules = MutableStateFlow<MutableList<Item>>(mutableListOf())
    val selectedOwnSchedules: StateFlow<List<Item>> = _selectedOwnSchedules

    private val _selectedOwnSchedulesSize = MutableStateFlow(0)
    val selectedOwnSchedulesSize: StateFlow<Int> = _selectedOwnSchedulesSize

    fun clearOwnSchedule(){
        _selectedOwnSchedules.value.clear()
    }

    fun addOrRemoveOwnSchedule(selected: Boolean, schedule: Item) {
        val scheduleToRemove = _selectedOwnSchedules.value.find { it.id == schedule.id }
        if (selected) {
            _selectedOwnSchedules.value.add(schedule)
            _selectedOwnSchedulesSize.value++
        } else {
            _selectedOwnSchedules.value.remove(
                scheduleToRemove
            )
            _selectedOwnSchedulesSize.value--
        }
    }

    private val _datePickerState = MutableStateFlow(false)
    val datePickerState: StateFlow<Boolean> = _datePickerState

    fun changeDatePickerState(state: Boolean) {
        _datePickerState.update { state }
    }

    private val _daysOnPage = MutableStateFlow<MutableList<LocalDate>>(mutableListOf())
    val daysOnPage: StateFlow<MutableList<LocalDate>> = _daysOnPage

    private val _startDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val startDate: StateFlow<LocalDate> = _startDate


    private val _endDate = MutableStateFlow<LocalDate>(_startDate.value.plusDays(pageSize))
    val endDate: StateFlow<LocalDate> = _endDate

    fun nextPage() {
        _selectedEmployeeShifts.value.clear()
        _selectedOwnSchedules.value.clear()
        _selectedOwnSchedulesSize.value = 0
        _selectedEmployeeShiftsSize.value = 0
        _startDate.update { _startDate.value.plusDays(pageSize) }
        _endDate.update { _startDate.value.plusDays(pageSize) }
        getAllDaysOnPage()
        getShifts()
        getSchedule()
    }

    fun previousPage() {
        _selectedOwnSchedulesSize.value = 0
        _selectedEmployeeShiftsSize.value = 0
        _selectedOwnSchedules.value.clear()
        _selectedEmployeeShifts.value.clear()
        _startDate.update { _startDate.value.minusDays(pageSize) }
        _endDate.update { _startDate.value.plusDays(pageSize) }
        getAllDaysOnPage()
        getShifts()
        getSchedule()
    }

    fun changeStartingDate(date: LocalDate) {
        _selectedOwnSchedulesSize.value = 0
        _selectedEmployeeShiftsSize.value = 0
        _selectedOwnSchedules.value.clear()
        _selectedEmployeeShifts.value.clear()
        _startDate.update { date }
        _endDate.update { _startDate.value.plusDays(pageSize) }
        getAllDaysOnPage()
        getShifts()
        getSchedule()
    }


    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    fun changeSearchText(text: String) {
        _searchText.value = text
        _filteredEmployees.value = _unfilteredEmployees.value.filter {
            "${it.firstName} ${it.lastName}".lowercase()
                .contains(text.lowercase()) || text.isEmpty()
        }
    }

    val screenState: StateFlow<TradeScreenState> = _screenState
    val alertState: StateFlow<AlertState> = _alertState

    var alertText = ""

    fun hideAlert() {
        _alertState.value = AlertState.NONE
    }

    fun changeScreenState(state: TradeScreenState) {
        _screenState.value = state
    }

    fun clearSelection(){
        _selectedOwnSchedulesSize.value = 0
        _selectedEmployeeShiftsSize.value = 0
        _selectedOwnSchedules.value.clear()
        _selectedEmployeeShifts.value.clear()
    }

    var shift: ShiftDetailsModel? = null
    var shifts: List<TradeShift> = listOf()
    var selectedEmployee: Employee = Employee(
        "",
        "",
        "",
    )

    private val _trade = MutableStateFlow(
        TradeDetailsElement(
            "",
            originEmployee = Employee(
                CredentialPrefs.userId,
                CredentialPrefs.firstName,
                CredentialPrefs.lastName
            ),
            recipientEmployee = selectedEmployee,
            originShifts = listOf(),
            recipientShifts = listOf(),
            actions = listOf("SubmitTradeRequest")
        )
    )


    private val _originShift = MutableStateFlow(
        ShiftDetailsModel(
            "",
            hoursPaid = 0.5f,
            regularOvertimeHours = 0.0f,
            overtimeMultiplier = 0.0f,
            overtimeHours = 0.0f,
            comments = "",
            startTime = OffsetDateTime.MAX,
            endTime = OffsetDateTime.MIN,
            startsOnPreviousDay = false,
            startsOnNextDay = false,
            endsOnPreviousDay = false,
            endsOnNextDay = false,
            positionId = "",
            positionCode = "",
            positionDescription = "",
            locationId = "",
            locationCode = "",
            locationDescription = "",
            color = "",
            date = LocalDate.now(),
            shiftTimeId = "",
            shiftTimeCode = "",
            shiftTimeDescription = "",
            actions = listOf()
        )
    )
    var originalShift: StateFlow<ShiftDetailsModel> = _originShift
    private var selectedEmployeeId: String = ""

    private fun getShiftDetailState(shiftId: String) {
        viewModelScope.launch {
            when (val results = tradesRepo.getShiftDetails(shiftId)) {
                is Maybe.Success -> {
                    results.data.let {
                        _originShift.value = it
                        shift = it
                        _screenState.value = TradeScreenState.Trade
                    }
                }
                is Maybe.Failure -> {

                    Timber.d(results.exception)

                }
            }
        }
    }

    private fun getSchedule() {
        _ownScheduleLoaded.update { false }
        viewModelScope.launch {
            when (val results = groupsRepo.getGroupSchedule(
                _startDate.value,
                _endDate.value,
                GroupFiltersResponse(
                    employees = mutableListOf(selectedEmployee)
                )
            )) {
                is Maybe.Success -> {
                    results.data.let {
                        val tempGroupSchedule = it.groupScheduleItemCollections.map { schedule ->
                            GroupScheduleItemCollection(
                                schedule.id,
                                schedule.employeeName,
                                schedule.scheduleItemCollections.map { day ->
                                    ScheduleItemCollection(
                                        day.date,
                                        day.items.filter { item ->
                                            listOf(
                                                "Shift",
                                                "Leave",
                                                "ProjectedShift",
                                                "ProjectedLeave"
                                            ).contains(item.type)
                                        },
                                        day.actions
                                    )
                                }
                            )
                        }
                        _schedule.value = tempGroupSchedule
                    }
                    _ownSchedules.update { _schedule.value.firstOrNull { it.id == CredentialPrefs.userId } }
                    _ownScheduleLoaded.update { true }
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                    _ownScheduleLoaded.update { true }
                }
            }
        }
    }

    private fun getTradeDetailState(tradeId: String) {
        viewModelScope.launch {
            when (val results = tradesRepo.getTradeDetails(tradeId)) {
                is Maybe.Success -> {
                    results.data.let {
                        _trade.value = it
                        _screenState.value = TradeScreenState.Details
                    }
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                }
            }
        }
    }

    fun tradeConfirmation() {
        _trade.value = TradeDetailsElement(
            "",
            originEmployee = Employee(
                CredentialPrefs.userId,
                CredentialPrefs.firstName,
                CredentialPrefs.lastName
            ),
            recipientEmployee = selectedEmployee,
            originShifts = _selectedOwnSchedules.value.map {
                TradeShift(
                    it.id!!, it.color,
                    it.shiftTimeCode!!, it.positionCode!!,
                    it.locationCode!!, it.startTime!!,
                    it.endTime!!, DateUtils.parseLocalDate(it.date)!!
                )
            },
            recipientShifts = _selectedEmployeeShifts.value,
            actions = listOf("SubmitTradeRequest")
        )
    }

    val employees: StateFlow<List<Employee>> = _filteredEmployees
    val shiftGroups: StateFlow<List<TradeShift>> = _shifts
    val trade: StateFlow<TradeDetailsElement> = _trade

    private fun getEmployees() {
        val userId = CredentialPrefs.userId
        viewModelScope.launch {
            when (val results = tradesRepo.getTradableEmployees(userId)) {
                is Maybe.Success -> {
                    results.data.let {
                        _unfilteredEmployees.value = it
                        _filteredEmployees.value = it
                    }
                    _screenState.update { TradeScreenState.Search }
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                }
            }
        }
    }

    private fun getShifts() {
        _shifts.update { listOf() }
        shifts = listOf()
        viewModelScope.launch {
            when (val results = tradesRepo.getTradableShifts(
                selectedEmployeeId,
                _startDate.value.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )) {
                is Maybe.Success -> {
                    results.data.let {
                        shifts = it
                        _shifts.value = it
                    }
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                }
            }
        }
    }

    fun selectEmployee(employeeId: String) {
        selectedEmployeeId = employeeId
        selectedEmployee = employees.value.first { it.id == employeeId }
        changeScreenState(TradeScreenState.Trade)
        getSchedule()
        getShifts()
    }

    fun initViewModel(shiftId: String, tradeId: String, analyticsProvider: AbstractAnalyticsProvider?) {
        this.analytics = analyticsProvider as FirebaseAnalyticsProvider?
        if (shiftId.isNotEmpty()) {
            setShift(shiftId)
        } else {
            getTradeDetailState(tradeId)
        }
    }

    private fun setShift(shiftId: String) {
        getShiftDetailState(shiftId)
//        getEmployees()
    }

    init {
        getAllDaysOnPage()
        getEmployees()
        getSchedule()
    }

    private fun getAllDaysOnPage() {
        _daysOnPage.value.clear()
        var currentDate = _startDate.value
        while (!currentDate.isAfter(_endDate.value)) {
            _daysOnPage.value.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }
    }

    fun acceptTrade(finish: (from: LocalDate?, to: LocalDate?) -> Unit) {
        viewModelScope.launch {
            when (val response = tradesRepo.acceptTrade(trade.value.id)) {
                is Maybe.Failure -> analytics?.logEvent(TradeRequestAnalyticEvents.OnError(response.exception?.message ?: "Failed to accept"))
                is Maybe.Success -> {
                    analytics?.logEvent(TradeRequestAnalyticEvents.OnApprove)
                    finish(
                        trade.value.recipientShifts.first().shiftDate,
                        trade.value.originShifts.first().shiftDate
                    )
                }
            }
        }
    }

    fun declineTrade(finish: (from: LocalDate?, to: LocalDate?) -> Unit) {
        viewModelScope.launch {
            when (val response = tradesRepo.declineTrade(trade.value.id)) {
                is Maybe.Failure -> analytics?.logEvent(TradeRequestAnalyticEvents.OnError(response.exception?.message ?: "Failed to Decline"))
                is Maybe.Success -> {
                    analytics?.logEvent(TradeRequestAnalyticEvents.OnReject)
                    finish(
                        trade.value.recipientShifts.first().shiftDate,
                        trade.value.originShifts.first().shiftDate
                    )
                }
            }
        }
    }

    fun validateTrade(finish: (from: LocalDate?, to: LocalDate?) -> Unit) {
        if (_selectedOwnSchedules.value.isNotEmpty()) {
            val tradeRequest =
                TradeRequest(
                    CredentialPrefs.userId,
                    _selectedOwnSchedules.value.map { it.id!! },
                    selectedEmployeeId,
                    _selectedEmployeeShifts.value.map { it.id }
                )

            viewModelScope.launch {
                _progressBarState.update { true }
                when (val result = tradesRepo.validateTrade(tradeRequest)) {
                    is Maybe.Failure -> {
                        alertText = "result.data.recipientViolations.first()"
                        _progressBarState.update { false }
                        _alertState.value = AlertState.WARN
                    }
                    is Maybe.Success -> {
                        if (result.data.recipientViolations.isEmpty() && result.data.canSubmitTrade) {
                            submitTrade(finish = finish)
                        } else {
                            alertText = result.data.recipientViolations.values.firstOrNull()?.firstOrNull() ?: ""
                            if (result.data.canSubmitTrade) {
                                _progressBarState.update { false }
                                _alertState.value = AlertState.WARN
                            } else {
                                _progressBarState.update { false }
                                _alertState.value = AlertState.ERROR
                            }
                        }
                    }
                }
            }
        }
    }

    fun forceSubmit(finish: (from: LocalDate?, to: LocalDate?) -> Unit) {
        analytics?.logEvent(TradeRequestAnalyticEvents.OnForceSubmit)
        submitTrade(finish)
    }

    private fun submitTrade(finish: (from: LocalDate?, to: LocalDate?) -> Unit) {
        _progressBarState.update { true }
        if (_selectedOwnSchedules.value.isNotEmpty()) {
            val tradeRequest =
                TradeRequest(
                    CredentialPrefs.userId,
                    _selectedOwnSchedules.value.map { it.id!! },
                    selectedEmployeeId,
                    _selectedEmployeeShifts.value.map { it.id }
                )

            viewModelScope.launch {
                when (val response = tradesRepo.postTrade(tradeRequest)) {
                    is Maybe.Failure -> analytics?.logEvent(TradeRequestAnalyticEvents.OnError(response.exception?.message ?: "Failed to submit"))
                    is Maybe.Success -> finish(
                        trade.value.recipientShifts.firstOrNull()?.shiftDate,
                        trade.value.originShifts.firstOrNull()?.shiftDate
                    )
                }
            }
        }
    }

    fun cancelTrade(finish: (from: LocalDate?, to: LocalDate?, ) -> Unit) {
        viewModelScope.launch {
            when (val response = tradesRepo.cancelTrade(trade.value.id)) {
                is Maybe.Failure -> analytics?.logEvent(TradeRequestAnalyticEvents.OnError(response.exception?.message ?: "Failed to cancel"))
                is Maybe.Success -> {
                    analytics?.logEvent(TradeRequestAnalyticEvents.OnCancel)
                    finish(
                        trade.value.recipientShifts.firstOrNull()?.shiftDate,
                        trade.value.originShifts.firstOrNull()?.shiftDate,
                    )
                }
            }
        }
    }
}