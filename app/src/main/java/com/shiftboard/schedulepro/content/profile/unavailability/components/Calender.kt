package com.shiftboard.schedulepro.content.profile.unavailability.components

import android.view.ContextThemeWrapper
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.shiftboard.schedulepro.R
import org.threeten.bp.LocalDate

@Composable
fun Calender(
    onDateChange: (LocalDate) -> Unit,
) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            CalendarView(
                ContextThemeWrapper(
                    it,
                    R.style.CalenderViewCustom
                ),
            )
        },
        modifier = Modifier.wrapContentWidth(),
        update = { view ->
            view.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
                Toast.makeText(context, "$dayOfMonth", Toast.LENGTH_SHORT).show()
                onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
            }
        }
    )
}