package com.shiftboard.schedulepro.content.profile.unavailability.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.profile.unavailability.toBinaryArray
import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.network.model.profile.PatternType
import com.shiftboard.schedulepro.core.network.model.profile.Recurrence
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

@Composable
fun RecurrenceView(
    recurrence: Recurrence,
    edit: (Recurrence) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { edit(recurrence) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recurrence.subject,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxSize(.5f)
            )
            RepeatPeriodView(
                patternType = when (recurrence.patternType) {
                    "Daily" -> PatternType.Daily
                    "Weekly" -> PatternType.Weekly
                    "Monthly" -> PatternType.Monthly
                    else -> PatternType.Yearly
                },
                patternInterval = if (recurrence.patternType == PatternType.Yearly.name) 1 else recurrence.patternInterval,
                dateOfTheYear = if (recurrence.patternType == PatternType.Yearly.name) LocalDate.of(
                    2022,
                    recurrence.patternInterval,
                    recurrence.patternDay
                ) else DateUtils.parseLocalDate(recurrence.startTime),
                dateOfTheMonth = when (recurrence.patternType) {
                    PatternType.Monthly.name -> recurrence.patternDay
                    PatternType.Daily.name -> recurrence.patternInterval
                    else -> DateUtils.parseLocalDate(recurrence.startTime)?.dayOfMonth
                        ?: 0
                }
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val startHour = DateUtils.parseLocalDateTime(recurrence.startTime)
                ?.format(DateTimeFormatter.ofPattern("HH"))?.toInt()
            val endHour = DateUtils.parseLocalDateTime(recurrence.endTime)
                ?.format(DateTimeFormatter.ofPattern("HH"))?.toInt()
            Log.d("RecurrenceView", ": $startHour $endHour")
            val startTimeRedirects = DateUtils.parseLocalDateTime(recurrence.startTime)
                ?.format(DateTimeFormatter.ofPattern("a", Locale.ENGLISH))
            val endTimeRedirects = DateUtils.parseLocalDateTime(recurrence.endTime)
                ?.format(DateTimeFormatter.ofPattern("a", Locale.ENGLISH))
            Text(
                text = if (startHour == 0 && endHour == 23) stringResource(id = R.string.all_day) else DateUtils.parseLocalDateTime(
                    recurrence.startTime
                )?.format(
                    DateTimeFormatter.ofPattern("hh:mm")
                ) + " " + startTimeRedirects?.uppercase(Locale.US) + "-" +
                        DateUtils.parseLocalDateTime(recurrence.endTime)
                            ?.format(DateTimeFormatter.ofPattern("hh:mm")) + " " + endTimeRedirects?.uppercase(),
                style = MaterialTheme.typography.body2.copy(fontSize = 13.7.sp),
                modifier = Modifier.padding(vertical = 3.dp)
            )

            Text(
                text = buildAnnotatedString {
                    append(
                        stringResource(R.string.repeat_until) + " : ${
                            DateUtils.parseLocalDate(
                                recurrence.patternEndDate
                            )?.dayOfMonth
                        }"
                    )
                    withStyle(
                        style = SpanStyle(
                            baselineShift = BaselineShift.Superscript,
                            fontSize = 10.sp
                        )
                    ) {
                        append(
                            ordinalSuffix(
                                number = DateUtils.parseLocalDate(recurrence.patternEndDate)?.dayOfMonth
                                    ?: 2,
                                context = context
                            )
                        )
                    }
                    append(
                        "${
                            DateUtils.parseLocalDate(recurrence.patternEndDate)?.month?.getDisplayName(
                                org.threeten.bp.format.TextStyle.SHORT_STANDALONE,
                                Locale.getDefault()
                            )
                        }, ${DateUtils.parseLocalDate(recurrence.patternEndDate)?.year}"
                    )
                }, style = MaterialTheme.typography.body2.copy(fontSize = 14.6.sp),
                textAlign = TextAlign.Center
            )
        }
        if (recurrence.patternType == stringResource(R.string.weekly)) {
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                modifier = Modifier

                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DaysView(binaryArray = recurrence.daysOfWeekBitmask.toBinaryArray())
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Divider()
    }
}

