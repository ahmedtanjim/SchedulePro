package com.shiftboard.schedulepro.content.profile.unavailability.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.shiftboard.schedulepro.R
import org.threeten.bp.LocalDate

@Composable
fun DateDialog(
    changeCalenderDialogState: (CalenderDialogState) -> Unit,
    calenderDialogState: CalenderDialogState,
    changeStartDate: (LocalDate) -> Unit,
    changeEndDate: (LocalDate) -> Unit,
    startDate: LocalDate,
    endDate: LocalDate
) {
    var tempDate by remember {
        mutableStateOf(LocalDate.now())
    }
    Dialog(onDismissRequest = { changeCalenderDialogState(CalenderDialogState.Nothing) }) {
        Box(modifier = Modifier.background(Color.White)) {
            Column(modifier = Modifier.wrapContentWidth()) {
                Calender(
                    onDateChange = {
                        tempDate = it
                    },
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp, end = 16.dp)
                ) {
                    TextButton(
                        onClick = { changeCalenderDialogState(CalenderDialogState.Nothing) }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.button,
                            color = Color.DarkGray
                        )
                    }

                    TextButton(
                        onClick = {
                            if (calenderDialogState == CalenderDialogState.StartDate) {
                                changeStartDate(tempDate)
                            } else if (calenderDialogState == CalenderDialogState.EndDate) {
                                changeEndDate(tempDate)
                            }
                            changeCalenderDialogState(CalenderDialogState.Nothing)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.apply),
                            style = MaterialTheme.typography.button,
                            color = Color.DarkGray
                        )
                    }

                }
            }
        }

    }

}