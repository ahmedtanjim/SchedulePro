package com.shiftboard.schedulepro.content.profile.unavailability.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.content.theme.Primary
import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.network.model.profile.Unavailability
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

@Composable
fun UnavailabilityView(
    unavailability: Unavailability,
    onEditButtonClicked: (Unavailability) -> Unit,
    onRepeatButtonClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val date = DateUtils.parseLocalDateTime(unavailability.startTime)
            date?.atZone(ZoneId.systemDefault())
            val day = date?.dayOfWeek
            val dayShort = day?.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val dayOfMonth =
                if ((date?.dayOfMonth ?: 0) > 9) "${date?.dayOfMonth}" else "0${date?.dayOfMonth}"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$dayShort",
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = dayOfMonth,
                    style = MaterialTheme.typography.body1
                )
            }
            Box(
                modifier = Modifier
                    .width(275.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .border(
                        width = .7.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(15.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = if (!unavailability.isAllDay) DateUtils.parseLocalDateTime(
                                unavailability.startTime
                            )?.format(
                                DateTimeFormatter.ofPattern("hh:mm")
                            ) + " " + DateUtils.parseLocalDateTime(unavailability.startTime)
                                ?.format(
                                    DateTimeFormatter.ofPattern("a", Locale.ENGLISH)
                                )?.uppercase(Locale.getDefault()) + "-" +
                                    DateUtils.parseLocalDateTime(unavailability.endTime)
                                        ?.format(DateTimeFormatter.ofPattern("hh:mm")) + " " + DateUtils.parseLocalDateTime(
                                unavailability.endTime
                            )?.format(
                                DateTimeFormatter.ofPattern("a", Locale.ENGLISH)
                            )?.uppercase(Locale.getDefault()) else "All Day",
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = unavailability.subject,
                            style = MaterialTheme.typography.body1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(15.dp)
                                .align(BottomStart)
                        ) {
                            Spacer(modifier = Modifier.height(2.dp))
                            if (unavailability.unavailabilityRecurrenceId != "00000000-0000-0000-0000-000000000000") {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(Primary.copy(alpha = .2f))
                                        .clickable {
                                            unavailability.unavailabilityRecurrenceId?.let {
                                                onRepeatButtonClicked(
                                                    it
                                                )
                                            }
                                        }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.swap),
                                        contentDescription = "",
                                        modifier = Modifier.align(Center),
                                        tint = Primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .clickable {
                                        onEditButtonClicked(unavailability)
                                    }
                                    .width(24.dp)
                                    .height(24.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Primary.copy(alpha = .2f))
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit_icon),
                                    contentDescription = "",
                                    modifier = Modifier.align(Center),
                                    tint = Primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}