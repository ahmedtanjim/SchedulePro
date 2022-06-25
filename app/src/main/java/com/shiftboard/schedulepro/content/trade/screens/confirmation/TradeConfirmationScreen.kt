package com.shiftboard.schedulepro.content.trade.screens.confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.content.trade.TradeViewModel
import com.shiftboard.schedulepro.content.trade.screens.confirmation.components.ConfirmTradeRow
import com.shiftboard.schedulepro.core.network.model.TradeDetailsElement

@Composable
fun TradeConfirmationScreen(
    trade: TradeDetailsElement,
    onCancelClicked: () -> Unit,
    viewModel: TradeViewModel,
    submitTrade: () -> Unit,
    progressBarState: Boolean,
) {
    BackHandler {
        onCancelClicked()
    }
    val selectedEmployeeSchedules by viewModel.selectedEmployeeShifts.collectAsState()
    val allDaysOnPage by viewModel.daysOnPage.collectAsState()
    val ownScheduleLoaded by viewModel.ownScheduleLoaded.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val ownSchedule by viewModel.ownSchedules.collectAsState()
    val employee = viewModel.selectedEmployee
    val tradeShifts by viewModel.shiftGroups.collectAsState()
    val selectedOwnSchedule by viewModel.selectedOwnSchedules.collectAsState()
    val selectedEmployeeShiftSize by viewModel.selectedEmployeeShiftsSize.collectAsState()
    val selectedOwnScheduleSize by viewModel.selectedOwnSchedulesSize.collectAsState()
    val originalShift by viewModel.originalShift.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Color.White) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        text = "Projected Effect on Schedule",
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.background(Color(246, 246, 246))) {
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth(0.25f))
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ownSchedule?.employeeName?.let { name ->
                            Row(
                                modifier = Modifier.fillMaxWidth(.49f),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = name,
                                    color = Color.DarkGray,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Clip
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(.95f),
                            horizontalArrangement = Arrangement.Center,
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
                        ConfirmTradeRow(
                            currentDate = item,
                            mySchedules = ownSchedule?.scheduleItemCollections?.find {
                                it.date.dayOfYear == item.dayOfYear
                            },
                            employeeShifts = tradeShifts.filter {
                                it.shiftDate.dayOfYear == item.dayOfYear
                            },
                            selectedOwnSchedules = selectedOwnSchedule,
                            selectedEmployeeSchedules = selectedEmployeeSchedules
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp)
                ) {
                    Button(
                        onClick = { onCancelClicked() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFFE9EEF6),
                            contentColor = Color(0XFF8CA8CF)
                        )
                    ) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { submitTrade() }, colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0XFF265AA5),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Submit Trade")
                    }
                }
            }
            if (progressBarState) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0XFF265AA5)
                )
            }
        }
    }
}