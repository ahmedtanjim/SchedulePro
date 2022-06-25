package com.shiftboard.schedulepro.content.profile.unavailability.screens

import android.content.Context
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.group.components.CustomCalendarView
import com.shiftboard.schedulepro.content.group.components.DatePicker
import com.shiftboard.schedulepro.content.profile.unavailability.KeyBoard
import com.shiftboard.schedulepro.content.profile.unavailability.components.CalenderDialogState
import com.shiftboard.schedulepro.content.profile.unavailability.components.TimeDialogState
import com.shiftboard.schedulepro.content.profile.unavailability.components.TimePickerDialog
import com.shiftboard.schedulepro.content.profile.unavailability.keyBoardAsState
import com.shiftboard.schedulepro.content.profile.unavailability.toBinaryArray
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.AddUnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.SharedViewModel
import com.shiftboard.schedulepro.content.theme.Primary
import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.network.model.profile.PatternType
import kotlinx.coroutines.flow.collectLatest
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun AddUnavailabilityScreen(
    viewModel: AddUnavailabilityScreenViewModel,
    navController: NavHostController,
    sharedViewModel: SharedViewModel,
) {
    val isEditing by sharedViewModel.isEditing.collectAsState()
    val editingRecurrence by sharedViewModel.recurrence.collectAsState()
    val selectedRecurrence by sharedViewModel.selectedRecurrence.collectAsState()
    val selectedUnavailability by sharedViewModel.selectedUnavailability.collectAsState()
    val context = LocalContext.current
    val focusRequester = FocusRequester()
    val patternIntervalFocused by viewModel.patternIntervalFocused.collectAsState()
    val sunday by viewModel.sunday.collectAsState()
    val monday by viewModel.monday.collectAsState()
    val tuesDay by viewModel.tuesday.collectAsState()
    val wednesday by viewModel.wednesday.collectAsState()
    val thursday by viewModel.thursday.collectAsState()
    val friday by viewModel.friday.collectAsState()
    val saturday by viewModel.saturday.collectAsState()
    val isKeyBoardOpen by keyBoardAsState()
    val timeDialogState by viewModel.timeDialogState.collectAsState()
    val time by viewModel.time.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val patternTypes by viewModel.patternTypes.collectAsState()
    val selectedPatternType by viewModel.selectedPatternType.collectAsState()
    val patternInterval by viewModel.patternInterval.collectAsState()
    val subject by viewModel.subject.collectAsState()
    val focusManager = LocalFocusManager.current
    val allDaySwitchState by viewModel.allDaySwitchState.collectAsState()
    val recurrenceSwitchState by viewModel.recurrenceSwitchState.collectAsState()
    val calenderDialogState by viewModel.calenderDialogState.collectAsState()
    val dayOfMonth by viewModel.dayOfMonth.collectAsState()
    val month by viewModel.month.collectAsState()
    val months by viewModel.months.collectAsState()
    val monthOfYear by viewModel.monthOfYear.collectAsState()
    var monthDropDownExpended by remember {
        mutableStateOf(false)
    }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dayOfYear by viewModel.dayOfYear.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    if (timeDialogState != TimeDialogState.Nothing) {
        TimePickerDialog(
            time = time,
            changeTimeDialogState = { viewModel.changeTimeDialog(it) },
            onTimeChange = { viewModel.changeTime(it) },
            timeDialogState = timeDialogState,
            changeStartTime = { viewModel.changeStartTime(it) },
            changeEndTime = { viewModel.changeEndTime(it) }
        )
    }
    if (calenderDialogState != CalenderDialogState.Nothing) {
        DatePicker(
            onDateSelected = {
                if (calenderDialogState == CalenderDialogState.StartDate) viewModel.changeStartDate(
                    it
                ) else viewModel.changeEndDate(it)
            },
            onDismissRequest = {
                viewModel.changeCalenderDialogState(CalenderDialogState.Nothing)
            },
            title = if (calenderDialogState == CalenderDialogState.StartDate) stringResource(id = R.string.date_picker_dialog_title) else stringResource(
                R.string.select_the_ending_date
            )
        )
    }
    BackHandler {
        navController.navigateUp()
    }
    LaunchedEffect(key1 = true) {
        viewModel.changeSubject("")
        viewModel.changeAllDaySwitchState(false)
        viewModel.changeSelectedDate(LocalDate.now())
        viewModel.changeStartTime(LocalTime.of(9, 0))
        viewModel.changeEndTime(LocalTime.of(17, 30))
        viewModel.changeRecurrenceSwitchState(false)
        viewModel.changePatternType(PatternType.Daily)
        viewModel.changeStartDate(LocalDate.now())
        viewModel.changeEndDate(LocalDate.now())
        viewModel.changeDayOfMonth("1")
        viewModel.changeDayOfYear("1")
        viewModel.changeMonth("1")
        viewModel.changePatternInterval("1")
        viewModel.resetWeek()
        if (isEditing) {
            if (editingRecurrence) {
                selectedRecurrence?.let {
                    val patternT = when (it.patternType) {
                        "Daily" -> PatternType.Daily
                        "Weekly" -> PatternType.Weekly
                        "Monthly" -> PatternType.Monthly
                        else -> PatternType.Yearly
                    }
                    viewModel.changeSubject(it.subject)
                    viewModel.changeRecurrenceSwitchState(true)
                    viewModel.changePatternType(patternT)
                    DateUtils.parseLocalDateTime(it.startTime)?.toLocalTime()?.let { startT ->
                        viewModel.changeStartTime(startT)
                        DateUtils.parseLocalDateTime(it.endTime)?.toLocalTime()?.let { endT ->
                            viewModel.changeEndTime(endT)
                            if (startT.hour == 0 && endT.hour == 23) {
                                viewModel.changeAllDaySwitchState(true)
                            } else viewModel.changeAllDaySwitchState(false)
                        }
                    }
                    DateUtils.parseLocalDate(it.patternStartDate)?.let { startD ->
                        viewModel.changeStartDate(startD)
                    }
                    DateUtils.parseLocalDate(it.patternEndDate)?.let { endD ->
                        viewModel.changeEndDate(endD)
                    }
                    when (patternT) {
                        PatternType.Daily -> {
                            viewModel.changeDayOfMonth(it.patternDay.toString())
                            viewModel.changePatternInterval(it.patternInterval.toString())
                        }
                        PatternType.Weekly -> {
                            val binaryArray = it.daysOfWeekBitmask.toBinaryArray()
                            if (binaryArray.component1()) viewModel.changeSunDay()
                            if (binaryArray.component2()) viewModel.changeMonday()
                            if (binaryArray.component3()) viewModel.changeTuesday()
                            if (binaryArray.component4()) viewModel.changeWednesday()
                            if (binaryArray.component5()) viewModel.changeThursday()
                            if (binaryArray[5]) viewModel.changeFriday()
                            if (binaryArray[6]) viewModel.changeSaturday()
                            viewModel.changePatternInterval(it.patternInterval.toString())
                        }
                        PatternType.Monthly -> {
                            viewModel.changeDayOfMonth(it.patternDay.toString())
                            viewModel.changeMonth(it.patternInterval.toString())
                        }
                        PatternType.Yearly -> {
                            viewModel.changeDayOfYear(it.patternDay.toString())
                            viewModel.changeMonthOfYear(Month.of(it.patternInterval))
                        }
                    }
                }
            } else {
                selectedUnavailability?.let {
                    viewModel.changeSubject(it.subject)
                    if (it.isAllDay) {
                        DateUtils.parseLocalDateTime(it.startTime)?.let { localDateTime ->
                            viewModel.changeSelectedDate(localDateTime.toLocalDate())
                        }
                        viewModel.changeAllDaySwitchState(true)
                    } else {
                        DateUtils.parseLocalDateTime(it.startTime)?.let { localDateTime ->
                            viewModel.changeSelectedDate(localDateTime.toLocalDate())
                            viewModel.changeStartTime(localDateTime.toLocalTime())
                        }
                        DateUtils.parseLocalDateTime(it.endTime)?.let { endDateTime ->
                            viewModel.changeEndTime(endDateTime.toLocalTime())
                        }
                    }
                }
            }
        }

        if (!isEditing && editingRecurrence) {
            viewModel.changeRecurrenceSwitchState(true)
        }

        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddUnavailabilityScreenViewModel.UIEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is AddUnavailabilityScreenViewModel.UIEvent.ChangeScreenState -> {
                    navController.navigateUp()
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (isSubmitting) {
            Dialog(onDismissRequest = { }) {
                CircularProgressIndicator(
                    color = Primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            if (isEditing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "",
                            tint = Primary
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.manage_unavailability),
                        style = MaterialTheme.typography.h6.copy(fontSize = 18.sp),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    TextButton(
                        onClick = {
                            viewModel.changeIsSubmitting(true)
                            if (editingRecurrence) {
                                selectedRecurrence?.let {
                                    viewModel.deleteRecurrence(it.id!!)
                                }
                            } else {
                                selectedUnavailability?.let {
                                    viewModel.deleteUnavailability(it.id!!)
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            color = Color.Red,
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.create_unavailability),
                        style = MaterialTheme.typography.h5
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider()
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            focusRequester.requestFocus()
                        }
                ) {
                    Row(
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                if (subject.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.subject),
                                        color = Color.DarkGray,
                                        style = MaterialTheme.typography.body1,
                                        modifier = Modifier.padding(top = 2.5.dp)
                                    )
                                }
                                BasicTextField(
                                    value = subject,
                                    onValueChange = { viewModel.changeSubject(it) },
                                    modifier = Modifier
                                        .focusRequester(focusRequester)
                                        .padding(top = 2.5.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.body1,
                                    keyboardActions = KeyboardActions(onDone = {
                                        focusManager.clearFocus(true)
                                    }),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                                )

                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(1.5.dp))
                Divider(thickness = 1.5.dp)
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.all_day),
                        style = MaterialTheme.typography.body1
                    )
                    Switch(
                        checked = allDaySwitchState,
                        onCheckedChange = { viewModel.changeAllDaySwitchState(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Primary,
                            checkedTrackColor = Primary
                        )
                    )
                }
                if (!allDaySwitchState) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.start_time),
                            style = MaterialTheme.typography.body1
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Color.LightGray.copy(0.5f)
                                )
                                .clickable {
                                    viewModel.changeTimeDialog(TimeDialogState.StartTime)
                                }
                        ) {
                            Text(
                                text = "${startTime.format(DateTimeFormatter.ofPattern("hh:mm"))} ${
                                    startTime.format(
                                        DateTimeFormatter.ofPattern("a")
                                    ).uppercase()
                                }",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.end_time),
                            style = MaterialTheme.typography.body1
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray.copy(.5f))
                                .clickable {
                                    viewModel.changeTimeDialog(TimeDialogState.EndTime)
                                }
                        ) {
                            Text(
                                text = "${endTime.format(DateTimeFormatter.ofPattern("hh:mm"))} ${
                                    endTime.format(
                                        DateTimeFormatter.ofPattern("a")
                                    ).uppercase()
                                }",
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recurrence),
                        style = MaterialTheme.typography.body1
                    )
                    Switch(
                        checked = recurrenceSwitchState,
                        onCheckedChange = { viewModel.changeRecurrenceSwitchState(it) },
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Primary,
                            checkedThumbColor = Primary
                        )
                    )
                }
                if (recurrenceSwitchState) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 11.5.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.5.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(5.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp),
                                    verticalAlignment = CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    patternTypes.forEach {
                                        RadioButton(
                                            selected = selectedPatternType == it,
                                            onClick = {
                                                viewModel.changePatternType(it)
                                                viewModel.changePatternInterval("1")
                                            },
                                            modifier = Modifier.size(23.dp),
                                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                                        )
                                        Text(
                                            text = when (it) {
                                                PatternType.Daily -> stringResource(id = R.string.day)
                                                PatternType.Weekly -> stringResource(R.string.week)
                                                PatternType.Monthly -> stringResource(R.string.month)
                                                else -> stringResource(R.string.year)
                                            },
                                            style = MaterialTheme.typography.body1.copy(fontSize = 15.sp)
                                        )
                                    }
                                }
                                when (selectedPatternType) {
                                    patternTypes[0] -> {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.every),
                                                style = MaterialTheme.typography.body1
                                            )
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 1.dp)
                                            ) {
                                                BasicTextField(
                                                    value = patternInterval,
                                                    onValueChange = {
                                                        if (it.length < 4) viewModel.changePatternInterval(
                                                            it
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .onFocusChanged {
                                                            if (patternIntervalFocused != it.isFocused) {
                                                                viewModel.changePatternIntervalFocus(
                                                                    it.isFocused
                                                                )
                                                            }
                                                        }
                                                        .width(
                                                            when (patternInterval.length) {
                                                                1 -> 12.dp
                                                                2 -> 20.dp
                                                                3 -> 30.dp
                                                                else -> 12.dp
                                                            }
                                                        ),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.body1,
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        focusManager.clearFocus(true)
                                                        if (dayOfMonth == "") viewModel.changeDayOfMonth(
                                                            "1"
                                                        )
                                                    }),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                )
                                                Divider(
                                                    modifier = Modifier.width(
                                                        when (patternInterval.length) {
                                                            1 -> 28.dp
                                                            2 -> 36.dp
                                                            3 -> 46.dp
                                                            else -> 28.dp
                                                        }
                                                    ),
                                                    color = Color.DarkGray
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(1.dp))
                                            Text(
                                                text = stringResource(R.string.days),
                                                style = MaterialTheme.typography.body1
                                            )
                                        }
                                    }
                                    patternTypes[1] -> {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.reoccour_every),
                                                style = MaterialTheme.typography.body1
                                            )
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                BasicTextField(
                                                    value = patternInterval,
                                                    onValueChange = {
                                                        if (it.length < 4) viewModel.changePatternInterval(
                                                            it
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .onFocusChanged {
                                                            if (patternIntervalFocused != it.isFocused) {
                                                                viewModel.changePatternIntervalFocus(
                                                                    it.isFocused
                                                                )
                                                            }
                                                        }
                                                        .width(
                                                            when (patternInterval.length) {
                                                                1 -> 12.dp
                                                                2 -> 20.dp
                                                                3 -> 30.dp
                                                                else -> 12.dp
                                                            }
                                                        ),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.body1,
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        focusManager.clearFocus(true)
                                                        if (patternInterval == "") viewModel.changePatternInterval(
                                                            "1"
                                                        )
                                                    }),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                )
                                                Divider(
                                                    modifier = Modifier.width(
                                                        when (patternInterval.length) {
                                                            1 -> 28.dp
                                                            2 -> 36.dp
                                                            3 -> 46.dp
                                                            else -> 28.dp
                                                        }
                                                    ),
                                                    color = Color.DarkGray
                                                )
                                            }
                                            Text(
                                                text = stringResource(R.string.weeks_on),
                                                style = MaterialTheme.typography.body1
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(9.dp))
                                        Row {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (sunday) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable {
                                                        viewModel.changeSunDay()
                                                    }
                                            ) {
                                                Text(
                                                    text = "S",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (sunday) Color.White else Color.Unspecified
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(3.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (monday) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.changeMonday() }
                                            ) {
                                                Text(
                                                    text = "M",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (monday) Color.White else Color.Unspecified
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(3.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (tuesDay) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.changeTuesday() }
                                            ) {
                                                Text(
                                                    text = "T",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (tuesDay) Color.White else Color.Unspecified
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(3.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (wednesday) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.changeWednesday() }
                                            ) {
                                                Text(
                                                    text = "W",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (wednesday) Color.White else Color.Unspecified
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(3.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (thursday) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.changeThursday() }
                                            ) {
                                                Text(
                                                    text = "T",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (thursday) Color.White else Color.Unspecified
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(3.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (friday) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.changeFriday() }
                                            ) {
                                                Text(
                                                    text = "F",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (friday) Color.White else Color.Unspecified
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(3.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(if (saturday) Primary else Color.Transparent)
                                                    .border(
                                                        width = 1.3.dp,
                                                        color = Primary,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { viewModel.changeSaturday() }
                                            ) {
                                                Text(
                                                    text = "S",
                                                    modifier = Modifier.align(Alignment.Center),
                                                    color = if (saturday) Color.White else Color.Unspecified
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                    }
                                    patternTypes[2] -> {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.day),
                                                style = MaterialTheme.typography.body1
                                            )
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 5.dp)
                                            ) {
                                                BasicTextField(
                                                    value = dayOfMonth,
                                                    onValueChange = {
                                                        if (it.length < 3) viewModel.changeDayOfMonth(
                                                            it
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .onFocusChanged {
                                                            if (patternIntervalFocused != it.isFocused) {
                                                                viewModel.changePatternIntervalFocus(
                                                                    it.isFocused
                                                                )
                                                            }
                                                        }
                                                        .width(
                                                            when (dayOfMonth.length) {
                                                                1 -> 12.dp
                                                                2 -> 20.dp
                                                                3 -> 30.dp
                                                                else -> 12.dp
                                                            }
                                                        ),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.body1,
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        focusManager.clearFocus(true)
                                                        if (dayOfMonth == "") viewModel.changeDayOfMonth(
                                                            "1"
                                                        )
                                                    }),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                )
                                                Divider(
                                                    modifier = Modifier.width(
                                                        when (patternInterval.length) {
                                                            1 -> 28.dp
                                                            2 -> 36.dp
                                                            3 -> 46.dp
                                                            else -> 28.dp
                                                        }
                                                    ),
                                                    color = Color.DarkGray
                                                )
                                            }
                                            Text(
                                                text = stringResource(R.string.of_every),
                                                style = MaterialTheme.typography.body1
                                            )
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 5.dp)
                                            ) {
                                                BasicTextField(
                                                    value = month,
                                                    onValueChange = {
                                                        if (it.length < 3) viewModel.changeMonth(
                                                            it
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .onFocusChanged {
                                                            if (patternIntervalFocused != it.isFocused) {
                                                                viewModel.changePatternIntervalFocus(
                                                                    it.isFocused
                                                                )
                                                            }
                                                        }
                                                        .width(
                                                            when (month.length) {
                                                                1 -> 12.dp
                                                                2 -> 20.dp
                                                                else -> 12.dp
                                                            }
                                                        ),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.body1,
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        focusManager.clearFocus(true)
                                                        if (patternInterval == "") viewModel.changePatternInterval(
                                                            "1"
                                                        )
                                                    }),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                )
                                                Divider(
                                                    modifier = Modifier.width(
                                                        when (patternInterval.length) {
                                                            1 -> 28.dp
                                                            2 -> 36.dp
                                                            3 -> 46.dp
                                                            else -> 28.dp
                                                        }
                                                    ),
                                                    color = Color.DarkGray
                                                )
                                            }
                                            Text(
                                                text = stringResource(R.string.months),
                                                style = MaterialTheme.typography.body1
                                            )
                                        }
                                    }
                                    patternTypes[3] -> {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.every),
                                                style = MaterialTheme.typography.body1
                                            )
                                            Text(
                                                text = getMonth(
                                                    month = monthOfYear,
                                                    context = context
                                                ),
                                                style = MaterialTheme.typography.body1,
                                                color = Primary,
                                                modifier = Modifier
                                                    .clickable { monthDropDownExpended = true }
                                                    .padding(horizontal = 8.dp)
                                            )
                                            DropdownMenu(
                                                expanded = monthDropDownExpended,
                                                onDismissRequest = {
                                                    monthDropDownExpended = false
                                                },
                                                modifier = Modifier.height(600.dp)
                                            ) {
                                                months.forEach {
                                                    DropdownMenuItem(onClick = {
                                                        viewModel.changeMonthOfYear(it)
                                                        monthDropDownExpended = false
                                                    }) {
                                                        Text(
                                                            text = getMonth(
                                                                month = it,
                                                                context = context
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = stringResource(R.string.on_day),
                                                style = MaterialTheme.typography.body1
                                            )
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(horizontal = 12.dp)
                                            ) {
                                                BasicTextField(
                                                    value = dayOfYear,
                                                    onValueChange = {
                                                        if (it.length < 3) viewModel.changeDayOfYear(
                                                            it
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .onFocusChanged {
                                                            if (patternIntervalFocused != it.isFocused) {
                                                                viewModel.changePatternIntervalFocus(
                                                                    it.isFocused
                                                                )
                                                            }
                                                        }
                                                        .width(
                                                            when (dayOfYear.length) {
                                                                1 -> 12.dp
                                                                2 -> 20.dp
                                                                else -> 12.dp
                                                            }
                                                        ),
                                                    singleLine = true,
                                                    textStyle = MaterialTheme.typography.body1,
                                                    keyboardActions = KeyboardActions(onDone = {
                                                        focusManager.clearFocus(true)
                                                        if (patternInterval == "") viewModel.changePatternInterval(
                                                            "1"
                                                        )
                                                    }),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done,
                                                        keyboardType = KeyboardType.Number
                                                    ),
                                                )
                                                Divider(
                                                    modifier = Modifier.width(
                                                        when (patternInterval.length) {
                                                            1 -> 28.dp
                                                            2 -> 36.dp
                                                            3 -> 46.dp
                                                            else -> 28.dp
                                                        }
                                                    ),
                                                    color = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .background(Color.White)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = stringResource(R.string.patter_of_recurrence),
                                style = MaterialTheme.typography.body1.copy(fontSize = 17.sp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 11.5.dp)
                                .fillMaxWidth()
                                .border(
                                    width = 1.5.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(5.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.start_time),
                                        style = MaterialTheme.typography.body1
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                Color.LightGray.copy(0.5f)
                                            )
                                            .clickable {
                                                viewModel.changeCalenderDialogState(
                                                    CalenderDialogState.StartDate
                                                )
                                            }
                                    ) {
                                        Text(
                                            text = "${startDate.month} ${startDate.dayOfMonth}, ${startDate.year}",
                                            style = MaterialTheme.typography.body1,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .align(Alignment.Center),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.end_time),
                                        style = MaterialTheme.typography.body1
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.LightGray.copy(.5f))
                                            .clickable {
                                                viewModel.changeCalenderDialogState(
                                                    CalenderDialogState.EndDate
                                                )
                                            }
                                    ) {
                                        Text(
                                            text = "${endDate.month} ${endDate.dayOfMonth}, ${endDate.year}",
                                            style = MaterialTheme.typography.body1,
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .align(Alignment.Center),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .background(Color.White)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = stringResource(R.string.range_of_recurrence),
                                style = MaterialTheme.typography.body1.copy(fontSize = 17.sp)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CustomCalendarView(
                            onDateSelected = {
                                viewModel.changeSelectedDate(it)
                            },
                            startingDate = selectedDate
                        )
                    }
                }

            }
        }
        Text(
            text = stringResource(R.string.submit),
            modifier = Modifier
                .align(BottomCenter)
                .padding(bottom = 60.dp)
                .clickable {
                    viewModel.submitUnavailability(
                        id = if (isEditing) {
                            if (editingRecurrence) {
                                selectedRecurrence?.id
                            } else selectedUnavailability?.id
                        } else null,
                        unavailabilityRecurrenceId = if (isEditing) selectedUnavailability?.unavailabilityRecurrenceId else null
                    )
                },
            color = Primary,
            style = MaterialTheme.typography.body1
        )
    }

    LaunchedEffect(key1 = isKeyBoardOpen) {
        if (patternIntervalFocused && isKeyBoardOpen == KeyBoard.Closed) {
            focusManager.clearFocus()
            if (patternInterval == "") viewModel.changePatternInterval("1")
            if (dayOfMonth == "") viewModel.changeDayOfMonth("1")
            if (month == "") viewModel.changeMonth("1")
            if (dayOfYear == "") viewModel.changeDayOfYear("1")
        }
    }


}

fun getMonth(month: Month, context: Context): String {
    return when (month) {
        Month.JANUARY -> context.getString(R.string.january)
        Month.FEBRUARY -> context.getString(R.string.february)
        Month.MARCH -> context.getString(R.string.march)
        Month.APRIL -> context.getString(R.string.april)
        Month.MAY -> context.getString(R.string.may)
        Month.JUNE -> context.getString(R.string.june)
        Month.JULY -> context.getString(R.string.july)
        Month.AUGUST -> context.getString(R.string.august)
        Month.SEPTEMBER -> context.getString(R.string.september)
        Month.OCTOBER -> context.getString(R.string.october)
        Month.NOVEMBER -> context.getString(R.string.november)
        Month.DECEMBER -> context.getString(R.string.december)
    }
}
