package com.shiftboard.schedulepro.content.profile.unavailability.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.profile.unavailability.viewmodels.UnavailabilityScreenViewModel
import com.shiftboard.schedulepro.content.theme.Primary
import com.shiftboard.schedulepro.core.network.model.profile.PatternType
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month

@ExperimentalMaterialApi
@Composable
fun BottomSheet(
    closeBottomSheet: () -> Unit,
    endDate: LocalDate,
    onDateDialog: () -> Unit,
    focusRequester: FocusRequester,
    viewModel: UnavailabilityScreenViewModel
) {
    val focusManager = LocalFocusManager.current
    var allDay by remember {
        mutableStateOf(false)
    }
    var subject by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val repeatTypes = listOf(PatternType.Daily, PatternType.Weekly, PatternType.Monthly, PatternType.Yearly)
    var repeatTypeExpended by remember {
        mutableStateOf(false)
    }
    var selectedRepeatType by remember {
        mutableStateOf(repeatTypes[0])
    }
    var sunday by remember {
        mutableStateOf(
            false
        )
    }
    var monday by remember {
        mutableStateOf(false)
    }
    var tuesDay by remember {
        mutableStateOf(false)
    }
    var wednesday by remember {
        mutableStateOf(false)
    }
    var thursday by remember {
        mutableStateOf(false)
    }
    var friday by remember {
        mutableStateOf(false)
    }
    var saturday by remember {
        mutableStateOf(false)
    }
    val repeatPeriod = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    var repeatPeriodExpended by remember {
        mutableStateOf(false)
    }
    var selectedRepeatPeriod by remember {
        mutableStateOf(repeatPeriod[0])
    }
    var startTimeExpanded by remember {
        mutableStateOf(false)
    }
    var endTimeExpanded by remember {
        mutableStateOf(false)
    }
    val endTimes = listOf(LocalTime.of(5, 0))
    val startTimes = listOf(LocalTime.of(9, 0))
    var startTime by remember {
        mutableStateOf(startTimes[0])
    }
    var endTime by remember {
        mutableStateOf(endTimes[0])
    }
    var dateDropDownExpanded by remember {
        mutableStateOf(false)
    }
    val dates = listOf(
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        35
    )
    var date by remember {
        mutableStateOf(dates[0])
    }
    val months = listOf(
        Month.JANUARY,
        Month.FEBRUARY,
        Month.MARCH,
        Month.APRIL,
        Month.MAY,
        Month.JUNE,
        Month.JULY,
        Month.AUGUST,
        Month.SEPTEMBER,
        Month.OCTOBER,
        Month.NOVEMBER,
        Month.DECEMBER,
    )
    var month by remember {
        mutableStateOf(months[0])
    }
    var monthDropDownExpended by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.crete_availability),
                    style = MaterialTheme.typography.h6
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        closeBottomSheet()
                        subject = ""
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = ""
                    )
                }
            },
            actions = {
                TextButton(onClick = {
                    if (subject.isEmpty()) {
                        Toast.makeText(context, "Enter the subject", Toast.LENGTH_SHORT).show()
                    } else if (selectedRepeatType == PatternType.Weekly && !sunday && !monday && !tuesDay && !wednesday && !thursday && !friday && !saturday) {
                        Toast.makeText(context, "Select Active Days", Toast.LENGTH_SHORT).show()
                    } else {

                        closeBottomSheet()
                        subject = ""
                        focusManager.clearFocus(true)
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.body1,
                        color = Primary
                    )
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black,
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = {
                subject = it
            },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            label = {
                Text(text = "Subject")
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Primary,
                focusedBorderColor = Primary,
                focusedLabelColor = Primary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) })
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = repeatTypeExpended,
            onExpandedChange = { repeatTypeExpended = !repeatTypeExpended },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            TextField(
                readOnly = true,
                value = selectedRepeatType.name,
                onValueChange = {},
                label = { Text(text = "Repeat Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = repeatTypeExpended,
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedIndicatorColor = Primary,
                    focusedLabelColor = Primary,
                    focusedTrailingIconColor = Primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = repeatTypeExpended,
                onDismissRequest = { repeatTypeExpended = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                repeatTypes.forEach {
                    DropdownMenuItem(onClick = {
                        selectedRepeatType = it
                        repeatTypeExpended = false
                    }) {
                        Text(text = it.name)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        ExposedDropdownMenuBox(
            expanded = repeatPeriodExpended,
            onExpandedChange = { repeatPeriodExpended = !repeatPeriodExpended },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = selectedRepeatPeriod.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Repeat Period") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = repeatPeriodExpended
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedLabelColor = Primary,
                    focusedIndicatorColor = Primary,
                    focusedTrailingIconColor = Primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = repeatPeriodExpended,
                onDismissRequest = { repeatTypeExpended = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                repeatPeriod.forEach {
                    DropdownMenuItem(
                        onClick = {
                            selectedRepeatPeriod = it
                            repeatPeriodExpended = false
                        }
                    ) {
                        Text(text = it.toString())
                    }
                }
            }
        }
        if (selectedRepeatType == PatternType.Yearly) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ExposedDropdownMenuBox(
                    expanded = dateDropDownExpanded,
                    onExpandedChange = { dateDropDownExpanded = !dateDropDownExpanded },
                    modifier = Modifier
                        .width(150.dp)
                ) {
                    TextField(
                        value = date.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = "Date"
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = dateDropDownExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedLabelColor = Primary,
                            focusedTrailingIconColor = Primary,
                            focusedIndicatorColor = Primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dateDropDownExpanded,
                        onDismissRequest = { dateDropDownExpanded = false },
                        modifier = Modifier
                    ) {
                        dates.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    date = it
                                    dateDropDownExpanded = false
                                }
                            ) {
                                Text(text = it.toString())
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = monthDropDownExpended,
                    onExpandedChange = { monthDropDownExpended = !monthDropDownExpended },
                    modifier = Modifier.width(150.dp)
                ) {
                    TextField(
                        value = month.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = "Month") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = monthDropDownExpended
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedIndicatorColor = Primary,
                            focusedLabelColor = Primary,
                            focusedTrailingIconColor = Primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = monthDropDownExpended,
                        onDismissRequest = { monthDropDownExpended = false }
                    ) {
                        months.forEach {
                            DropdownMenuItem(onClick = {
                                monthDropDownExpended = false
                                month = it
                            }) {
                                Text(text = it.name)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = endDate.toString(),
                onValueChange = {},
                label = { Text(text = "End Date") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "",
                        modifier = Modifier.clickable { onDateDialog() }
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Primary,
                    leadingIconColor = Primary,
                    focusedLabelColor = Primary
                ),
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .focusOrder(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            onDateDialog()
                        }
                    },
            )
        }
        if (!allDay) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                ExposedDropdownMenuBox(
                    expanded = startTimeExpanded,
                    onExpandedChange = { startTimeExpanded = !startTimeExpanded },
                    modifier = Modifier.width(150.dp)
                ) {
                    TextField(
                        value = startTime.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = "From"
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = startTimeExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedLabelColor = Primary,
                            focusedIndicatorColor = Primary,
                            focusedTrailingIconColor = Primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = startTimeExpanded,
                        onDismissRequest = { startTimeExpanded = false }
                    ) {
                        startTimes.forEach {
                            DropdownMenuItem(onClick = {
                                startTime = it
                                startTimeExpanded = false
                            }) {
                                Text(text = it.toString())
                            }
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = endTimeExpanded,
                    onExpandedChange = { endTimeExpanded = !endTimeExpanded },
                    modifier = Modifier.width(150.dp)
                ) {
                    TextField(
                        value = endTime.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = "To"
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = endTimeExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedTrailingIconColor = Primary,
                            focusedIndicatorColor = Primary,
                            focusedLabelColor = Primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = endTimeExpanded,
                        onDismissRequest = { endTimeExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        endTimes.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    endTimeExpanded = false
                                    endTime = it
                                }
                            ) {
                                Text(text = it.toString())
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = allDay, onCheckedChange = { allDay = it })
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "All Day")
        }

        if (selectedRepeatType == PatternType.Weekly) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (sunday) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            sunday = !sunday
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
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (monday) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            monday = !monday
                        }
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
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (tuesDay) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            tuesDay = !tuesDay
                        }
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
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (wednesday) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            wednesday = !wednesday
                        }
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
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (thursday) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            thursday = !thursday
                        }
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
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (friday) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            friday = !friday
                        }
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
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(if (saturday) Primary else Color.Transparent)
                        .border(width = 1.dp, color = Primary, shape = CircleShape)
                        .clickable {
                            saturday = !saturday
                        }
                ) {
                    Text(
                        text = "S",
                        modifier = Modifier.align(Alignment.Center),
                        color = if (saturday) Color.White else Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}