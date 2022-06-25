package com.shiftboard.schedulepro.content.trade.screens.trade.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.content.trade.safeParseColor
import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.details.ShiftDetailsModel
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun ShiftView(
    modifier: Modifier,
    shift: TradeShift,
    showDate: Boolean = true,
    showName: Boolean = true,
    extraContent: @Composable () -> Unit = {},
    nameBackGround: Color = Color.Gray,
    employee: Employee?,
    originalShift: ShiftDetailsModel? = null
) {
    fun Color.Companion.parse(colorString: String?): Color =
        Color(color = safeParseColor(colorString))

    val dayOfWeek: String = shift.shiftDate.format(DateTimeFormatter.ofPattern("EEE"))
    val dayOfMonth = shift.shiftDate.dayOfMonth
    val startTime: String
    val endTime: String
    if (originalShift == null) {
        startTime = shift.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        endTime = shift.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    } else {
        startTime = originalShift.startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        endTime = originalShift.endTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    Row(modifier = modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)) {
        if (showDate) {
            Column(
                content = {
                    Text(
                        text = dayOfWeek.uppercase(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Text(
                        text = "%02d".format(dayOfMonth),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                },
                modifier = Modifier
                    .padding(end = 28.dp)
                    .width(42.dp)
            )
        }
        Column {
            if (showName) {
                Surface(
                    content = {
                        employee?.let {
                            Text(
                                "${it.firstName} ${it.lastName}",
                                modifier = Modifier.padding(vertical = 2.dp, horizontal = 16.dp),
                                color = if (nameBackGround == Color.Gray) Color.Black else Color.White
                            )
                        }
                    },
                    shape = RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50),
                    color = nameBackGround,
                    modifier = Modifier
                        .padding(0.dp)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )
            }
            Surface(
                shape = RoundedCornerShape(4),
                elevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            ) {
                Row(
                    Modifier
                        .width(280.dp)
                        .height(IntrinsicSize.Min)
                        .background(Color.parse(shift.Color ?:"#FFFFFF").copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(8.dp)
                            .background(Color.parse(shift.Color ?:"#FFFFFF"))
                    )
                    Column(
                        content = {
                            Row {
                                Text(
                                    text = shift.shiftCode,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$startTime - $endTime",
                                    modifier = Modifier
                                        .padding(start = 8.dp),
                                )
                            }
                            Text(text = shift.positionCode)
                            Text(text = shift.locationCode)
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 16.dp)
                    ) {
                        extraContent()
                    }
                }
            }
        }
    }
}