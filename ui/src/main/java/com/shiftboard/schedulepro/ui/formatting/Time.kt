package com.shiftboard.schedulepro.ui.formatting

import android.content.Context
import android.text.SpannedString
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.shiftboard.schedulepro.ui.R
import org.threeten.bp.Duration
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.*

object TimeFormatters {
    fun spannedShortRange(
        startTime: OffsetDateTime, startDiff: Boolean,
        endTime: OffsetDateTime, endDiff: Boolean, militaryTime: Boolean
    ): SpannedString {
        return buildSpannedString {
            if (startDiff) {
                bold {
                    append(startTime.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE,
                        Locale.getDefault()).toUpperCase(Locale.getDefault()))
                    append(" ")
                }
            }
            if (militaryTime) {
                append(startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            } else {
                append(startTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
            }

            append(" - ")

            if (endDiff) {
                bold {
                    append(endTime.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE,
                        Locale.getDefault()).toUpperCase(Locale.getDefault()))
                    append(" ")
                }
            }
            if (militaryTime) {
                append(endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
            } else {
                append(endTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
            }
        }
    }

    fun spannedRange(
        context: Context,
        startTime: OffsetDateTime,
        endTime: OffsetDateTime, militaryTime: Boolean
    ): SpannedString {
        return buildSpannedString {
            val duration = Duration.between(startTime, endTime)
            if (duration.toHours() == 24L) {
                append(context.getString(R.string.all_day))
            } else {
                if (militaryTime) {
                    append(startTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                } else {
                    append(startTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
                }
                append(" ")
                append(context.getString(R.string.date_range_span_separator))
                append(" ")
                if (militaryTime) {
                    append(endTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                } else {
                    append(endTime.format(DateTimeFormatter.ofPattern("hh:mm a")))
                }
            }
        }
    }
}