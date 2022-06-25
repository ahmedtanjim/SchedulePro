package com.shiftboard.schedulepro.content.trade.screens.trade.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.group.Item
import com.shiftboard.schedulepro.core.network.model.group.ScheduleItemCollection
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun TradeRow(
    currentDate: LocalDate,
    ownSchedules: ScheduleItemCollection?,
    tradeAbleShifts: List<TradeShift>,
    selectedOwnSchedules: List<Item>,
    addOrRemoveOwnSchedule: (Boolean, Item) -> Unit,
    selectedEmployeeSchedules: List<TradeShift>,
    addOrRemoveEmployeeSchedule: (Boolean, TradeShift) -> Unit,
    originalShiftId: String
) {
    var maxShiftPerRow by remember {
        mutableStateOf(1)
    }
    if (ownSchedules != null) {
        if (maxShiftPerRow < ownSchedules.items.size) {
            maxShiftPerRow = ownSchedules.items.size
        }
    }
    if (maxShiftPerRow < tradeAbleShifts.size) {
        maxShiftPerRow = tradeAbleShifts.size

    }
    fun toDay(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("EEE"))
    }

    fun toDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("dd"))

    }
    Row(
        modifier = Modifier
            .height(
                when (maxShiftPerRow) {
                    1 -> 70.dp
                    2 -> 140.dp
                    3 -> 210.dp
                    4 -> 280.dp
                    5 -> 350.dp
                    else -> 70.dp
                }
            )
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(.25f)
                .padding(end = 0.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = toDay(currentDate),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = toDate(currentDate),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        SelectableShiftView(
            ownShifts = ownSchedules,
            maxShiftPerRow = maxShiftPerRow,
            fraction = 0.49f,
            ownShift = true,
            tradAbleShifts = tradeAbleShifts,
            selectedOwnSchedules = selectedOwnSchedules,
            addOrRemoveOwnSchedule = addOrRemoveOwnSchedule,
            selectedEmployeeSchedules = selectedEmployeeSchedules,
            addOrRemoveEmployeeSchedule = addOrRemoveEmployeeSchedule,
            originalShiftId = originalShiftId
        )
        Spacer(modifier = Modifier.width(5.dp))
        SelectableShiftView(
            ownShifts = ownSchedules,
            maxShiftPerRow = maxShiftPerRow,
            fraction = .95f,
            ownShift = false,
            tradAbleShifts = tradeAbleShifts,
            selectedOwnSchedules = selectedOwnSchedules,
            addOrRemoveOwnSchedule = addOrRemoveOwnSchedule,
            selectedEmployeeSchedules = selectedEmployeeSchedules,
            addOrRemoveEmployeeSchedule = addOrRemoveEmployeeSchedule,
            originalShiftId = originalShiftId
        )
        Spacer(modifier = Modifier.fillMaxWidth(1f))
    }
    Divider(modifier = Modifier.fillMaxWidth())
}