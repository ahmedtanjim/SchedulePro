package com.shiftboard.schedulepro.content.group.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.group.components.EmployeeFilter
import com.shiftboard.schedulepro.content.group.components.FilterComponents
import com.shiftboard.schedulepro.content.group.model.enums.BottomSheetState
import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.group.GroupFilter

@Composable
fun FilterScreen(
    state: BottomSheetState,
    onStateChange: (BottomSheetState) -> Unit,

    locations: List<GroupFilter>,
    onLocationChange: (location: GroupFilter, selected: Boolean) -> Unit,
    onAllLocationsChanged: (locations: List<GroupFilter>, Boolean) -> Unit,

    positions: List<GroupFilter>,
    onPositionChange: (position: GroupFilter, selected: Boolean) -> Unit,
    onAllPositionsChanged: (positions: List<GroupFilter>, Boolean) -> Unit,

    shiftTimes: List<GroupFilter>,
    onShiftTimeChange: (shiftTimes: GroupFilter, selected: Boolean) -> Unit,
    onAllShiftTypesChanged: (shiftTimes: List<GroupFilter>, Boolean) -> Unit,

    leaveTypes: List<GroupFilter>,
    onLeaveTypeChange: (leaveTypes: GroupFilter, selected: Boolean) -> Unit,
    onAllLeaveTypesChanged: (leaveTypes: List<GroupFilter>, Boolean) -> Unit,

    teams: List<GroupFilter>,
    onTeamChange: (team: GroupFilter, selected: Boolean) -> Unit,
    onAllTeamsChanged: (teams: List<GroupFilter>, Boolean) -> Unit,

    employees: List<Employee>,
    filteredEmployees: List<Employee>,
    onEmployeeChange: (employee: Employee, selected: Boolean) -> Unit,
    onAllEmployeesChanged: (Boolean, List<Employee>) -> Unit,
    filteredLocations: List<GroupFilter>,
    filteredShiftTimes: List<GroupFilter>,
    filteredLeaveTypes: List<GroupFilter>,
    filteredTeams: List<GroupFilter>,
    filteredPositions: List<GroupFilter>,
    onBackClicked: () -> Unit,
) {
    BackHandler {
        onBackClicked()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                contentColor = Color.Black,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.align(Alignment.CenterStart)) {
                        Spacer(modifier = Modifier.width(14.dp))
                    }
                }
            }
        }
    ) {
        when (state) {
            BottomSheetState.FilterLocations -> {
                FilterComponents(
                    list = locations,
                    groupFilterList = filteredLocations,
                    onFilterChange = onLocationChange,
                    onAllSelected = onAllLocationsChanged,
                    onApplyFilter = { onStateChange(BottomSheetState.Filter) },
                    filterType = stringResource(id = R.string.locations)
                )
            }
            BottomSheetState.FilterPositions -> {
                FilterComponents(
                    list = positions,
                    groupFilterList = filteredPositions,
                    onFilterChange = onPositionChange,
                    onAllSelected = onAllPositionsChanged,
                    onApplyFilter = { onStateChange(BottomSheetState.Filter) },
                    filterType = stringResource(id = R.string.positions)
                )
            }
            BottomSheetState.FilterShiftTimes -> {
                FilterComponents(
                    list = shiftTimes,
                    groupFilterList = filteredShiftTimes,
                    onFilterChange = onShiftTimeChange,
                    onAllSelected = onAllShiftTypesChanged,
                    onApplyFilter = { onStateChange(BottomSheetState.Filter) },
                    filterType = stringResource(id = R.string.shift_times)
                )
            }
            BottomSheetState.FilterLeaveTypes -> {
                FilterComponents(
                    list = leaveTypes,
                    groupFilterList = filteredLeaveTypes,
                    onFilterChange = onLeaveTypeChange,
                    onAllSelected = onAllLeaveTypesChanged,
                    onApplyFilter = { onStateChange(BottomSheetState.Filter) },
                    filterType = stringResource(id = R.string.leave_types)
                )
            }
            BottomSheetState.FilterTeams -> {
                FilterComponents(
                    list = teams,
                    groupFilterList = filteredTeams,
                    onFilterChange = onTeamChange,
                    onAllSelected = onAllTeamsChanged,
                    onApplyFilter = { onStateChange(BottomSheetState.Filter) },
                    filterType = stringResource(id = R.string.teams)
                )
            }
            BottomSheetState.FilterEmployees -> {
                EmployeeFilter(
                    employeeList = employees,
                    filteredEmployeeList = filteredEmployees,
                    onFilterChange = onEmployeeChange,
                    onAllSelected = onAllEmployeesChanged,
                    onApplyFilter = { onStateChange(BottomSheetState.Filter) }
                )
            }
            else -> {}
        }
    }
}