package com.shiftboard.schedulepro.content.group.components

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.shiftboard.schedulepro.R
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import java.util.*

@Composable
fun CustomCalendarView(onDateSelected: (LocalDate) -> Unit, startingDate: LocalDate = LocalDate.now()) {
    // Adds view to Compose
    val cal = Calendar.getInstance()
    cal.set(startingDate.year, startingDate.monthValue - 1, startingDate.dayOfMonth)
    AndroidView(
        modifier = Modifier.wrapContentSize(),
        factory = { context ->
            CalendarView(ContextThemeWrapper(context, R.style.CalenderViewCustom))
        },
        update = { view ->
            view.date = cal.timeInMillis
                view.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    onDateSelected(
                        LocalDate
                            .now()
                            .withMonth(month + 1)
                            .withYear(year)
                            .withDayOfMonth(dayOfMonth)
                    )
                }
        }
    )
}
