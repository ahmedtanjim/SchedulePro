package com.shiftboard.schedulepro.ui.schedule

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.shiftboard.schedulepro.ui.databinding.WidgetDayTagBinding
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*

class DayTagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
): LinearLayout(context, attrs, defStyle) {

    private val binding: WidgetDayTagBinding =
        WidgetDayTagBinding.inflate(LayoutInflater.from(context), this, true)

    fun setDate(date: LocalDate) {
        binding.dayTitle.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE,
            Locale.getDefault()).toUpperCase(Locale.getDefault())

        binding.dayNumber.text = date.dayOfMonth.toString()
    }
}