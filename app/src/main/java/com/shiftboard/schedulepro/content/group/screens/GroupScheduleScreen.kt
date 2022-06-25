package com.shiftboard.schedulepro.content.group.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.group.GroupViewModel
import com.shiftboard.schedulepro.content.group.components.*
import com.shiftboard.schedulepro.content.group.model.enums.BottomSheetState
import com.shiftboard.schedulepro.core.network.model.group.GroupFiltersResponse
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalMaterialApi
@Composable
fun GroupScheduleScreen(
    viewModel: GroupViewModel,
    goToTradeScreen: () -> Unit,
) {
    val datePickerState by viewModel.datePickerState.collectAsState()
    val scope = rememberCoroutineScope()
    val bottomState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, confirmStateChange = {
            it != ModalBottomSheetValue.HalfExpanded
        })
    var bottomSheetState by remember {
        mutableStateOf(BottomSheetState.Filter)
    }
    val employees by viewModel.filteredEmployees.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val schedule by viewModel.shiftGroups.collectAsState()
    val screenState by viewModel.screenState.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val positions by viewModel.positions.collectAsState()
    val shiftTimes by viewModel.shiftTimes.collectAsState()
    val leaveTypes by viewModel.leaveTypes.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val tradePermission by viewModel.tradePermission.collectAsState()

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetShape = MaterialTheme.shapes.large,
        sheetContent = {
            when (bottomSheetState) {
                BottomSheetState.Filter -> {
                    BottomSheet(
                        onBottomSheetStateChange = {
                            bottomSheetState = it
                        },
                        locations = locations,
                        positions = positions,
                        shiftTimes = shiftTimes,
                        leaveTypes = leaveTypes,
                        teams = teams,
                        onCancelClicked = {
                            scope.launch {
                                bottomState.animateTo(ModalBottomSheetValue.Hidden)
                            }
                        },
                        employees = employees,
                        onUpdateFiltersClicked = {
                            viewModel.getFilteredSchedule(
                                filters = GroupFiltersResponse(
                                    positions = positions,
                                    locations = locations,
                                    shiftTimes = shiftTimes,
                                    leaveTypes = leaveTypes,
                                    teams = teams,
                                    employees = employees,
                                )
                            )
                            scope.launch {
                                bottomState.animateTo(ModalBottomSheetValue.Hidden)
                            }
                        }
                    )
                }
                else -> {
                    FilterScreen(
                        state = bottomSheetState,
                        onStateChange = {
                            bottomSheetState = it
                        },


                        filteredLocations = locations,
                        locations = filters.locations,
                        onLocationChange = { location, selected ->
                            if (selected) {
                                viewModel.addLocations(location)
                            } else {
                                viewModel.removeLocation(location)
                            }
                        },
                        onAllLocationsChanged = { locations, selected ->
                            if (selected) {
                                viewModel.addAllLocation(locations = locations)
                            } else {
                                viewModel.removeAllLocation()
                            }
                        },


                        positions = filters.positions,
                        filteredPositions = positions,
                        onPositionChange = { position, selected ->
                            if (selected) {
                                viewModel.addPositions(position = position)
                            } else {
                                viewModel.removePosition(position = position)
                            }
                        },
                        onAllPositionsChanged = { positions, selected ->
                            if (selected) {
                                viewModel.addAllPositions(positions = positions)
                            } else {
                                viewModel.removeAllPositions()
                            }
                        },

                        shiftTimes = filters.shiftTimes,
                        filteredShiftTimes = shiftTimes,
                        onShiftTimeChange = { shiftTime, selected ->
                            if (selected) {
                                viewModel.addShiftTimes(shiftTime)
                            } else {
                                viewModel.removeShiftTimes(shiftTime)
                            }
                        },
                        onAllShiftTypesChanged = { shiftTimes, selected ->
                            if (selected) {
                                viewModel.addAllShiftTimes(shiftTimes = shiftTimes)
                            } else {
                                viewModel.removeAllShiftTimes()
                            }
                        },

                        leaveTypes = filters.leaveTypes,
                        filteredLeaveTypes = leaveTypes,
                        onLeaveTypeChange = { leaveType, selected ->
                            if (selected) {
                                viewModel.addLeaveTypes(leaveType)
                            } else {
                                viewModel.removeLeaveTypes(leaveType)
                            }
                        },
                        onAllLeaveTypesChanged = { leaveTypes, selected ->
                            if (selected) {
                                viewModel.addAllLeaveTypes(leaveTypes = leaveTypes)
                            } else {
                                viewModel.removeAllLeaveTypes()
                            }
                        },

                        teams = filters.teams,
                        filteredTeams = teams,
                        onTeamChange = { team, selected ->
                            if (selected) {
                                viewModel.addTeams(team)
                            } else {
                                viewModel.removeTeams(team)
                            }
                        },
                        onAllTeamsChanged = { teams, selected ->
                            if (selected) {
                                viewModel.addAllTeams(teams = teams)
                            } else {
                                viewModel.removeAllTeams()
                            }
                        },

                        employees = filters.employees,
                        filteredEmployees = employees,
                        onEmployeeChange = { employee, selected ->
                            if (selected) {
                                viewModel.addEmployee(employee)
                            } else {
                                viewModel.removeEmployee(employee)
                            }
                        },
                        onAllEmployeesChanged = { selected, allEmployees ->
                            if (selected) {
                                viewModel.addAllEmployees(allEmployees)
                            } else {
                                viewModel.removeAllEmployees()
                            }
                        },
                        onBackClicked = { bottomSheetState = BottomSheetState.Filter },
                    )
                }
            }
        },

        ) {
        if (datePickerState) {
            DatePicker(onDateSelected = viewModel::changeStartingDate) {
                viewModel.changeDatePickerState(false)
            }
        }
        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()


        fun toDay(date: OffsetDateTime): String {
            return date.format(DateTimeFormatter.ofPattern("EEE"))
        }

        fun toDate(date: OffsetDateTime): String {
            return date.format(DateTimeFormatter.ofPattern("dd"))

        }

        Scaffold(
            topBar = {
                TopBar(
                    onFilterClicked = {
                        scope.launch {
                            bottomState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    },
                    onNextDatesClicked = {
                        viewModel.nextPage(
                            filters = GroupFiltersResponse(
                                positions = positions,
                                locations = locations,
                                shiftTimes = shiftTimes,
                                leaveTypes = leaveTypes,
                                teams = teams,
                                employees = employees,
                            )
                        )
                    },
                    onPreviousDatesClicked = {
                        viewModel.previousPage(
                            filters = GroupFiltersResponse(
                                positions = positions,
                                locations = locations,
                                shiftTimes = shiftTimes,
                                leaveTypes = leaveTypes,
                                teams = teams,
                                employees = employees,
                            )
                        )
                    },
                    startDate = startDate,
                    endDate = endDate,
                    goToTradeScreen = goToTradeScreen,
                    tradeIconVisible = tradePermission
                ) {
                    viewModel.changeDatePickerState(true)
                }
            }
        ) {
            when (screenState) {
                GroupScreenState.Details -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row {
                            Text(
                                text = stringResource(R.string.employees),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(top = 15.dp, start = 20.dp, end = 20.dp)
                                    .width(100.dp),
                                style = MaterialTheme.typography.h6
                            )
                            Row(
                                modifier = Modifier
                                    .padding(top = 15.dp)
                                    .horizontalScroll(horizontalScrollState)
                            ) {
                                schedule.firstOrNull()?.scheduleItemCollections?.map { it.date }
                                    ?.forEach { item ->
                                        Column(
                                            modifier = Modifier
                                                .width(120.dp)
                                                .padding(end = 10.dp)
                                        ) {
                                            Text(
                                                text = toDay(item),
                                                style = MaterialTheme.typography.h6,
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                            Text(
                                                text = toDate(item),
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            )
                                        }
                                    }
                            }
                        }
                        Spacer(modifier = Modifier.heightIn(20.dp))
                        Row {
                            Column(modifier = Modifier.verticalScroll(verticalScrollState)) {
                                schedule.forEach {
                                    EmployeeView(groupScheduleItemCollection = it)
                                }
                            }
                            Row(
                                modifier = Modifier.horizontalScroll(horizontalScrollState)
                            ) {
                                Column(modifier = Modifier.verticalScroll(verticalScrollState)) {
                                    schedule.forEach { groupScheduleItemCollection ->
                                        ShiftRow(groupScheduleItemCollection = groupScheduleItemCollection)
                                    }
                                }
                            }
                        }
                    }
                }
                GroupScreenState.Loading -> {
                    viewModel.initViewModel()
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            Modifier
                                .width(45.dp)
                                .height(45.dp)
                        )
                    }
                }
                GroupScreenState.Filtering -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            Modifier
                                .width(45.dp)
                                .height(45.dp)
                        )
                    }
                }
            }
        }
    }
}