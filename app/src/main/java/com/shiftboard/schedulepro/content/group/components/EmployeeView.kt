package com.shiftboard.schedulepro.content.group.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.core.network.model.group.GroupScheduleItemCollection

@Composable
fun EmployeeView(
    groupScheduleItemCollection: GroupScheduleItemCollection
) {
    var maxShiftPerRow = remember {
        mutableStateOf(1)
    }
    groupScheduleItemCollection.scheduleItemCollections.forEach {
        if (maxShiftPerRow.value < it.items.size) {
            maxShiftPerRow = mutableStateOf(it.items.size)
        }
    }
    Box(
        modifier = Modifier
            .border(width = 0.5.dp, color = Color.LightGray)
            .padding(bottom = 10.dp, top = 10.dp)
            .height(
                when (maxShiftPerRow.value) {
                    1 -> 60.dp
                    2 -> 120.dp
                    3 -> 180.dp
                    4 -> 240.dp
                    5 -> 300.dp
                    else -> 60.dp
                }
            )

            .padding(
                horizontal = 20.dp
            )
            .width(100.dp)
    ) {
        Text(
            text = groupScheduleItemCollection.employeeName,
            modifier = Modifier
                .padding(start = 0.dp)
                .align(Alignment.CenterStart),
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.body2
        )
    }
}