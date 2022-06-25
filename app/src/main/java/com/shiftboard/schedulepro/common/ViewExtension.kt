package com.shiftboard.schedulepro.common

import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.text.strikeThrough
import androidx.core.view.isVisible
import com.shiftboard.schedulepro.databinding.HourSummaryIncludeBinding
import com.shiftboard.schedulepro.databinding.IncludeCommentBlockBinding
import com.shiftboard.schedulepro.ui.HourSummaryBackground
import com.shiftboard.schedulepro.ui.R
import java.text.DecimalFormat


fun HourSummaryIncludeBinding.setDetails(hours: Float, overtime: Float = 0f, multiplier: Float = 0f, color: Int) {
    val format = DecimalFormat("####.##")

    regularHours.text = root.context.getString(R.string.hour_format, format.format(hours))

    overtimeHours.text = root.context.getString(R.string.overtime_format, format.format(overtime))
    overtimeMultiplier.text = root.context.getString(R.string.overtime_multiplier_format, format.format(multiplier))

    root.background = HourSummaryBackground(color)

    divider1.isVisible = overtime > 0f
    divider2.isVisible = overtime > 0f
    overtimeHours.isVisible = overtime > 0f
    overtimeMultiplier.isVisible = overtime > 0f
}


fun IncludeCommentBlockBinding.setComments(
    manager: String?,
    employee: String?,
    employeeAmended: String?
){
    if (manager.isNullOrBlank() && employee.isNullOrBlank()) {
        root.isVisible = false
        return
    }

    root.isVisible = true
    if (!manager.isNullOrBlank()) {
        managerComment.text = manager
        managerComment.isVisible = true
        managerCommentHeader.isVisible = !employee.isNullOrBlank()

    } else {
        managerComment.isVisible = false
        managerCommentHeader.isVisible = false
    }

    if (!employeeAmended.isNullOrBlank()) {
        val spannedString = SpannableStringBuilder()
            .append(employeeAmended + "\n")
            .strikeThrough {
                if (!employee.isNullOrBlank()) {
                    append(employee)
                }
            }
        employeeComment.setText(spannedString, TextView.BufferType.SPANNABLE)
        employeeComment.isVisible = true
        employeeCommentHeader.isVisible = !manager.isNullOrBlank()

    } else if (!employee.isNullOrBlank()) {
        employeeComment.text = employee
        employeeComment.isVisible = true
        employeeCommentHeader.isVisible = !manager.isNullOrBlank()

    } else {
        employeeComment.isVisible = false
        employeeCommentHeader.isVisible = false
    }

    ConstraintSet().apply { clone(root) }.applyTo(root)
}