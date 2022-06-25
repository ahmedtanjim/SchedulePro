package com.shiftboard.schedulepro.content.trade.screens.trade

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.group.components.DatePicker
import com.shiftboard.schedulepro.content.trade.TradeViewModel
import com.shiftboard.schedulepro.content.trade.components.TopBar
import com.shiftboard.schedulepro.content.trade.screens.main.TradeScreenState
import com.shiftboard.schedulepro.content.trade.screens.trade.components.EmployeeSearchBar
import com.shiftboard.schedulepro.content.trade.screens.trade.components.ShiftView
import com.shiftboard.schedulepro.content.trade.screens.trade.components.TradeRow
import com.shiftboard.schedulepro.content.trade.screens.trade.components.TradeTopBar
import com.shiftboard.schedulepro.core.common.utils.observe
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.persistence.model.UserPrefs
import com.shiftboard.schedulepro.core.persistence.preference.UserPreferences

@ExperimentalMaterialApi
@Composable
fun TradeScreen(
    confirmation: () -> Unit,
    onCancelClicked: () -> Unit,
    viewModel: TradeViewModel,
    fromGroup: Boolean,
) {
    val selectedEmployeeSchedules by viewModel.selectedEmployeeShifts.collectAsState()
    val allDaysOnPage by viewModel.daysOnPage.collectAsState()
    val datePickerState by viewModel.datePickerState.collectAsState()
    val ownScheduleLoaded by viewModel.ownScheduleLoaded.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val ownSchedule by viewModel.ownSchedules.collectAsState()
    val employee = viewModel.selectedEmployee
    val tradeShifts by viewModel.shiftGroups.collectAsState()
    val selectedOwnSchedules by viewModel.selectedOwnSchedules.collectAsState()
    val selectedEmployeeShiftsSize by viewModel.selectedEmployeeShiftsSize.collectAsState()
    val selectedOwnSchedulesSize by viewModel.selectedOwnSchedulesSize.collectAsState()
    val text = "${stringResource(id = R.string.trade)} ($selectedOwnSchedulesSize/${selectedEmployeeShiftsSize})"
    val originalShift by viewModel.originalShift.collectAsState()
    if (!fromGroup) {
        BackHandler {
            onCancelClicked()
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    var myName = ""
    viewModel.userDetailsLiveDate.observe(lifecycleOwner) {
        myName = "${it.firstName} ${it.lastName}"
    }
    LaunchedEffect(key1 = true) {
        if (employee.id == "") {
            viewModel.changeScreenState(TradeScreenState.Search)
        }
    }

    Scaffold(
        topBar = {
            if (ownScheduleLoaded && ownSchedule == null && originalShift.id.isNotEmpty()) {
                TopBar(topBarText = stringResource(id = R.string.trade))
            } else {
                TradeTopBar(
                    onEmployeesClicked = onCancelClicked,
                    onPreviousDatesClicked = viewModel::previousPage,
                    onNextDatesClicked = viewModel::nextPage,
                    startDate = startDate,
                    endDate = endDate,
                    onCalenderClicked = { viewModel.changeDatePickerState(true) }
                )
            }
        },
    ) {
        if (datePickerState) {
            DatePicker(
                onDateSelected = viewModel::changeStartingDate,
                onDismissRequest = { viewModel.changeDatePickerState(false) }
            )
        }
        Column(
            Modifier
                .background(Color(246, 246, 246))
        ) {
            if (ownScheduleLoaded && ownSchedule != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Box(
                        content = {
                            EmployeeSearchBar(
                                modifier = Modifier.height(36.dp),
                                clickAction = {
                                    viewModel.changeScreenState(TradeScreenState.Search)
                                    viewModel.clearSelection()
                                },
                                startingText = "${employee.firstName} ${employee.lastName}"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(.49f),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                text = myName,
                                    color = Color.DarkGray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Clip

                                )
                            }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(.95f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${employee.firstName} ${employee.lastName}",
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Clip
                            )
                        }
                        Spacer(modifier = Modifier.fillMaxWidth(1f))
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(allDaysOnPage) { _, item ->
                        TradeRow(
                            currentDate = item,
                            ownSchedules = ownSchedule?.scheduleItemCollections?.find {
                                it.date.dayOfYear == item.dayOfYear
                            },
                            tradeAbleShifts = tradeShifts.filter {
                                it.shiftDate.dayOfYear == item.dayOfYear
                            },
                            selectedOwnSchedules = selectedOwnSchedules,
                            addOrRemoveOwnSchedule = viewModel::addOrRemoveOwnSchedule,
                            selectedEmployeeSchedules = selectedEmployeeSchedules,
                            addOrRemoveEmployeeSchedule = viewModel::addOrRemoveEmployeeSchedule,
                            originalShiftId = originalShift.id
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 16.dp,
                            horizontal = 20.dp
                        )
                ) {
                    Button(
                        onClick = {
                            if (selectedOwnSchedulesSize == selectedEmployeeShiftsSize && selectedOwnSchedulesSize != 0) run {
                                confirmation()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (!(selectedOwnSchedulesSize == selectedEmployeeShiftsSize && selectedOwnSchedulesSize != 0)) Color(
                                0XFFE9EEF6
                            ) else Color(
                                0XFF265AA5
                            ),
                            contentColor = if (!(selectedOwnSchedulesSize == selectedEmployeeShiftsSize && selectedOwnSchedulesSize != 0)) Color(
                                0XFF8CA8CF
                            ) else Color.White
                        )
                    ) {
                        Text(text = text)
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onCancelClicked,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                0XFF265AA5
                            ),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.close))
                    }
                }
            } else if (ownScheduleLoaded && ownSchedule == null && originalShift.id.isNotEmpty()) {
                val listState = rememberLazyListState()
                var selectedIndex by remember {
                    mutableStateOf("")
                }
                ShiftView(
                    modifier = Modifier.padding(vertical = 16.dp),
                    shift = TradeShift(
                        id = originalShift.id,
                        Color = originalShift.color,
                        shiftCode = originalShift.shiftTimeCode,
                        positionCode = originalShift.positionCode,
                        locationCode = originalShift.locationCode,
                        startTime = originalShift.startTime,
                        endTime = originalShift.endTime,
                        shiftDate = originalShift.date
                    ),
                    showName = false,
                    employee = employee,
                    originalShift = originalShift
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        EmployeeSearchBar(
                            modifier = Modifier.height(36.dp),
                            clickAction = { viewModel.changeScreenState(TradeScreenState.Search) },
                            startingText = "${employee.firstName} ${employee.lastName}"
                        )
                    }
                }

                LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                    items(tradeShifts.size) { index ->
                        val item = tradeShifts[index]
                        ShiftView(
                            modifier = Modifier.selectable(
                                selected = item.id == selectedIndex,
                                onClick = {
                                    selectedIndex = if (selectedIndex != item.id) item.id else ""
                                    val newSelectedTradeShift = tradeShifts.find {
                                        it.id == selectedIndex
                                    }
                                    if (newSelectedTradeShift != null) {
                                        viewModel.changeSelectedShift(newSelectedTradeShift)
                                    }
                                },
                            ),
                            shift = item,
                            employee = employee,
                            extraContent = {
                                if (item.id == selectedIndex) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_signupcheckempty__1_),
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_signupcheckempty),
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            },
                            showName = index == 0
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    Button(
                        onClick = {
                            val shiftSelected = if (selectedIndex == "") "0" else "1"
                            if (shiftSelected == "1") {
                                confirmation()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedIndex == "") Color(0XFFE9EEF6) else Color(
                                0XFF265AA5
                            ),
                            contentColor = if (selectedIndex == "") Color(0XFF8CA8CF) else Color.White
                        )
                    ) {
                        val shiftSelected = if (selectedIndex == "") "0" else "1"
                        Text(text = "${stringResource(id = R.string.trade)} ($shiftSelected/1)")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { onCancelClicked() }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFF265AA5),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(id = R.string.close))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (originalShift.id.isBlank() && ownScheduleLoaded) {
                        Text(stringResource(R.string.no_tradable_shifts))
                    } else {
                        CircularProgressIndicator(color = Color(0XFF265AA5))
                    }
                }
            }
        }
    }
}
