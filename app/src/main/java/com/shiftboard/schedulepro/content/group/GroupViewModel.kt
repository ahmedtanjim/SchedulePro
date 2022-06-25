package com.shiftboard.schedulepro.content.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.activities.splash.repo.UserCacheRepo
import com.shiftboard.schedulepro.content.group.domain.GroupsRepo
import com.shiftboard.schedulepro.content.group.screens.GroupScreenState
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.group.GroupFilter
import com.shiftboard.schedulepro.core.network.model.group.GroupFiltersResponse
import com.shiftboard.schedulepro.core.network.model.group.GroupScheduleItemCollection
import com.shiftboard.schedulepro.core.network.model.group.ScheduleItemCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import timber.log.Timber


class GroupViewModel(private val groupsRepo: GroupsRepo, private val userCacheRepo: UserCacheRepo) :
    ViewModel() {
    private val _screenState = MutableStateFlow(GroupScreenState.Loading)
    private val _filters = MutableStateFlow(GroupFiltersResponse())
    private val _selectedFilters = MutableStateFlow(GroupFiltersResponse())
    private val _schedule = MutableStateFlow<List<GroupScheduleItemCollection>>(listOf())
    private val pageSize = 14L
    private val _startDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val startDate: StateFlow<LocalDate> = _startDate

    private val _datePickerState = MutableStateFlow(false)
    val datePickerState: StateFlow<Boolean> = _datePickerState

    fun changeDatePickerState(state: Boolean) {
        _datePickerState.update { state }
    }

    private val _tradePermission = MutableStateFlow(false)
    val tradePermission: StateFlow<Boolean> = _tradePermission

    private suspend fun tradePermission() {
        _tradePermission.value = userCacheRepo.permissions().trade.create
    }

    private val _endDate = MutableStateFlow<LocalDate>(_startDate.value.plusDays(pageSize))
    val endDate: StateFlow<LocalDate> = _endDate


    fun initViewModel() {
        getSchedule()
        getFilters()
        viewModelScope.launch {
            tradePermission()
        }
    }

    private fun changeScreenState(state: GroupScreenState) {
        _screenState.value = state
    }

    private fun getFilters() {
        viewModelScope.launch {
            when (val results = groupsRepo.getFilters()) {
                is Maybe.Success -> {
                    results.data.let {
                        _filters.value = it
                        _selectedFilters.value = it
                    }
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                }
            }
        }
    }

    private fun getSchedule() {
        changeScreenState(GroupScreenState.Loading)
        viewModelScope.launch {
            when (val results =
                groupsRepo.getGroupSchedule(_startDate.value, _endDate.value, _filters.value)) {
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
                        changeScreenState(GroupScreenState.Details)
                    }
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                    changeScreenState(GroupScreenState.Details)

                }
            }
        }
    }

    fun getFilteredSchedule(filters: GroupFiltersResponse) {
        changeScreenState(GroupScreenState.Filtering)
        viewModelScope.launch {
            when (val results =
                groupsRepo.getGroupSchedule(_startDate.value, _endDate.value, filters)) {
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
                        changeScreenState(GroupScreenState.Details)
                    }
                }
                is Maybe.Failure -> {
                    changeScreenState(GroupScreenState.Details)
                    Timber.d(results.exception)
                }
            }
        }
    }

    private val _filteredEmployees = MutableStateFlow<MutableList<Employee>>(mutableListOf())
    val filteredEmployees: StateFlow<MutableList<Employee>> = _filteredEmployees

    private val _locations = MutableStateFlow<MutableList<GroupFilter>>(mutableListOf())
    val locations: StateFlow<MutableList<GroupFilter>> = _locations

    fun addLocations(location: GroupFilter) {
        _locations.value.add(location)

    }

    fun removeLocation(location: GroupFilter) {
        _locations.value.remove(location)
    }

    fun addAllLocation(locations: List<GroupFilter>) {
        _locations.value.clear()
        _locations.value.addAll(locations)
    }

    fun removeAllLocation() {
        _locations.update { mutableListOf() }
    }

    private val _positions = MutableStateFlow<MutableList<GroupFilter>>(mutableListOf())
    val positions: StateFlow<MutableList<GroupFilter>> = _positions

    fun addPositions(position: GroupFilter) {
        _positions.value.add(position)
    }

    fun addAllPositions(positions: List<GroupFilter>) {
        _positions.value.clear()
        _positions.value.addAll(positions)
    }

    fun removePosition(position: GroupFilter) {
        _positions.value.remove(position)
    }

    fun removeAllPositions() {
        _positions.update { mutableListOf() }
    }

    private val _shiftTimes = MutableStateFlow<MutableList<GroupFilter>>(mutableListOf())
    val shiftTimes: StateFlow<MutableList<GroupFilter>> = _shiftTimes

    fun addShiftTimes(shiftTime: GroupFilter) {
        _shiftTimes.value.add(shiftTime)
    }

    fun addAllShiftTimes(shiftTimes: List<GroupFilter>) {
        _shiftTimes.value.clear()
        _shiftTimes.value.addAll(shiftTimes)
    }

    fun removeShiftTimes(shiftTimes: GroupFilter) {
        _shiftTimes.value.remove(shiftTimes)
    }

    fun removeAllShiftTimes() {
        _shiftTimes.update { mutableListOf() }
    }

    private val _leaveTypes = MutableStateFlow<MutableList<GroupFilter>>(mutableListOf())
    val leaveTypes: StateFlow<MutableList<GroupFilter>> = _leaveTypes

    fun addLeaveTypes(leaveTypes: GroupFilter) {
        _leaveTypes.value.add(leaveTypes)
    }

    fun addAllLeaveTypes(leaveTypes: List<GroupFilter>) {
        _leaveTypes.value.clear()
        _leaveTypes.value.addAll(leaveTypes)
    }

    fun removeLeaveTypes(leaveTypes: GroupFilter) {
        _leaveTypes.value.remove(leaveTypes)
    }

    fun removeAllLeaveTypes() {
        _leaveTypes.update { mutableListOf() }
    }

    private val _teams = MutableStateFlow<MutableList<GroupFilter>>(mutableListOf())
    val teams: StateFlow<MutableList<GroupFilter>> = _teams

    fun addTeams(teams: GroupFilter) {
        _teams.value.add(teams)
    }

    fun addAllTeams(teams: List<GroupFilter>) {
        _teams.value.clear()
        _teams.value.addAll(teams)
    }

    fun removeTeams(teams: GroupFilter) {
        _teams.value.remove(teams)
    }

    fun removeAllTeams() {
        _teams.update { mutableListOf() }
    }

    fun addEmployee(employee: Employee) {
        _filteredEmployees.value.add(employee)
    }

    fun addAllEmployees(employees: List<Employee>) {
        _filteredEmployees.value.clear()
        _filteredEmployees.value.addAll(employees)
    }

    fun removeAllEmployees() {
        _filteredEmployees.value.clear()
    }

    fun removeEmployee(employee: Employee) {
        _filteredEmployees.value.remove(employee)
    }

    fun changeStartingDate(date: LocalDate) {
        _startDate.update { date }
        _endDate.update { _startDate.value.plusDays(pageSize) }
        getSchedule()
    }

    fun nextPage(filters: GroupFiltersResponse) {
        _startDate.update { _startDate.value.plusDays(pageSize) }
        _endDate.update { _startDate.value.plusDays(pageSize) }
        getFilteredSchedule(filters)
    }

    fun previousPage(filters: GroupFiltersResponse) {
        _startDate.update { _startDate.value.minusDays(pageSize) }
        _endDate.update { _startDate.value.plusDays(pageSize) }
        getFilteredSchedule(filters)
    }

    val filters: StateFlow<GroupFiltersResponse> = _filters
    val shiftGroups: StateFlow<List<GroupScheduleItemCollection>> = _schedule
    val screenState: StateFlow<GroupScreenState> = _screenState
}