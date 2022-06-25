package com.shiftboard.schedulepro.content.trade.screens.confirmation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.network.model.TradeDetailsElement
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.group.Item
import com.shiftboard.schedulepro.core.network.model.group.ScheduleItemCollection
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun ConfirmTradeRow(
    currentDate: LocalDate,
    mySchedules: ScheduleItemCollection?,
    employeeShifts: List<TradeShift>,
    selectedOwnSchedules: List<Item>,
    selectedEmployeeSchedules: List<TradeShift>
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val ownSchedules = remember {
        mutableStateListOf<Item>()
    }
    val tradeAbleShifts = remember {
        mutableStateListOf<TradeShift>()
    }



    val tradedEmployeeShiftsOnCurrentDate by remember {
        mutableStateOf(
            selectedEmployeeSchedules.filter { it.shiftDate.dayOfYear == currentDate.dayOfYear }
        )
    }

    val tradedOwnShiftsOnCurrentDate by remember {
        mutableStateOf(
            selectedOwnSchedules.filter { DateUtils.parseLocalDate(it.date)?.dayOfYear == currentDate.dayOfYear }
        )
    }
    LaunchedEffect(lifecycleOwner.lifecycle) {
        mySchedules?.items?.forEach {
            ownSchedules.add(it)
        }

        employeeShifts.forEach {
            tradeAbleShifts.add(it)
        }

        tradedEmployeeShiftsOnCurrentDate.forEach {
            ownSchedules.add(it.toItem())
            tradeAbleShifts.remove(it)
        }

        tradedOwnShiftsOnCurrentDate.forEach { item ->
            val itemInSelectedOwnSchedule = ownSchedules.find { it.id == item.id }
            if (itemInSelectedOwnSchedule != null){
                ownSchedules.remove(itemInSelectedOwnSchedule)
                tradeAbleShifts.add(itemInSelectedOwnSchedule.toTradeShift())
            }
        }

    }


    var maxShiftPerRow by remember {
        mutableStateOf(1)
    }
    if (maxShiftPerRow < ownSchedules.size) {
        maxShiftPerRow = ownSchedules.size
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
                .align(CenterVertically)
        ) {
            Text(
                text = toDay(currentDate),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.align(CenterHorizontally),
            )
            Text(
                text = toDate(currentDate),
                modifier = Modifier.align(CenterHorizontally)
            )
        }

        TradeShiftView(
            ownShifts = ownSchedules,
            tradAbleShifts = employeeShifts,
            maxShiftPerRow = maxShiftPerRow,
            fraction = 0.49f,
            ownShift = true,
            selectedOwnSchedule = selectedOwnSchedules,
            selectedEmployeeSchedules = selectedEmployeeSchedules
        )
        Spacer(modifier = Modifier.width(5.dp))
        TradeShiftView(
            ownShifts = ownSchedules,
            tradAbleShifts = tradeAbleShifts,
            maxShiftPerRow = maxShiftPerRow,
            fraction = .95f,
            ownShift = false,
            selectedOwnSchedule = selectedOwnSchedules,
            selectedEmployeeSchedules = selectedEmployeeSchedules
        )
        Spacer(modifier = Modifier.fillMaxWidth(1f))
    }
    Divider(modifier = Modifier.fillMaxWidth())
}