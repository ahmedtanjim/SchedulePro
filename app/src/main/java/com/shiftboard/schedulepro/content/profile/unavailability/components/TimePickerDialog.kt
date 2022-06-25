package com.shiftboard.schedulepro.content.profile.unavailability.components

import android.view.ContextThemeWrapper
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.shiftboard.schedulepro.R
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun TimePickerDialog(
    time: LocalTime,
    changeTimeDialogState: (TimeDialogState) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    timeDialogState: TimeDialogState,
    changeStartTime: (LocalTime) -> Unit,
    changeEndTime: (LocalTime) -> Unit,
) {
    Dialog(onDismissRequest = { changeTimeDialogState(TimeDialogState.Nothing) }) {
        Box(modifier = Modifier.background(Color.White)) {
            Column {
                AndroidView({ c ->
                    TimePicker(
                        ContextThemeWrapper(
                            c,
                            R.style.CalenderViewCustom,
                        )
                    )
                },
                    Modifier.wrapContentSize(),
                    update = { view ->
                        view.setOnTimeChangedListener { _, hour, min ->
                            onTimeChange(LocalTime.of(hour, min))
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 16.dp, end = 16.dp)
                ) {
                    TextButton(
                        onClick = { changeTimeDialogState(TimeDialogState.Nothing) }
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.button,
                            color = Color.DarkGray
                        )
                    }

                    TextButton(
                        onClick = {
                            if (timeDialogState == TimeDialogState.StartTime) {
                                changeStartTime(
                                    time
                                )
                            } else if (timeDialogState == TimeDialogState.EndTime) {
                                changeEndTime(
                                    time
                                )
                            }
                            changeTimeDialogState(TimeDialogState.Nothing)
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