package com.shiftboard.schedulepro.content.group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.content.trade.safeParseColor
import com.shiftboard.schedulepro.core.network.model.group.ScheduleItemCollection

@Composable
fun  ShiftView(
    scheduleItemCollection: ScheduleItemCollection,
    maxShiftPerRow: Int,
    changMaxShiftPerRow: (Int) -> Unit
) {
    fun Color.Companion.parse(colorString: String): Color =
        Color(color = safeParseColor(colorString))
    Column(
        modifier = Modifier
            .padding(start = 10.dp)
            .width(110.dp)
            .height(
                when (maxShiftPerRow) {
                    1 -> 60.dp
                    2 -> 120.dp
                    3 -> 180.dp
                    4 -> 240.dp
                    5 -> 300.dp
                    else -> 60.dp
                }
            )
            .padding(end = 10.dp),
        verticalArrangement = Arrangement.Top
    ) {
        if (maxShiftPerRow < scheduleItemCollection.items.size) {
            changMaxShiftPerRow(scheduleItemCollection.items.size)
        }
        scheduleItemCollection.items.forEach { item ->
            Box(
                modifier = Modifier
                    .height(55.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(Color.parse(item.color))

                    ) {

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.parse(item.color).copy(alpha = 0.1f))
                    ) {
                        item.shiftTimeCode?.let {
                            Text(
                                text = it,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                        item.leaveTypeCode?.let {
                            Text(
                                text = it,
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Spacer(modifier = Modifier.width(5.dp))
                            item.positionCode?.let { Text(text = it) }
                            Spacer(modifier = Modifier.width(5.dp))
                            item.locationCode?.let { Text(text = it) }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }

}