@file:Suppress("unused")

package com.shiftboard.schedulepro.core.common.utils

import android.util.Range
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.Temporal
import java.util.Calendar
import java.util.Locale

val epoch: LocalDate = LocalDate.ofEpochDay(0)

// Formatters
fun OffsetDateTime.hourTimestamp(militaryTime: Boolean): String = if (militaryTime) {
    format(DateTimeFormatter.ofPattern("HH:mm"))
} else {
    format(DateTimeFormatter.ofPattern("hh:mm a"))
}

fun OffsetDateTime.modifiedTimestamp(militaryTime: Boolean): String {
    val now = OffsetDateTime.now()
    return atZoneSameInstant(ZoneId.systemDefault()).run {
        when {
            Duration.between(this, now)
                .toHours() < 24 -> if (militaryTime) {
                format(DateTimeFormatter.ofPattern("HH:mm"))
            } else {
                format(DateTimeFormatter.ofPattern("hh:mm a"))
            }
            year == now.year -> "${month.displayFull()} $dayOfMonth"
            else -> "${month.displayFull()} $dayOfMonth, $year"
        }
    }
}

fun OffsetDateTime.shortDateTimeDisplay(militaryTime: Boolean): String =
    if (militaryTime) {
        format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    } else {
        format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"))
    }

fun LocalDate.shortDateTimeDisplay(militaryTime: Boolean): String =
    if (militaryTime) {
        format(DateTimeFormatter.ofPattern("MMM dd, HH:mm"))
    } else {
        format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a"))
    }


fun OffsetDateTime.displayDayOfWeek(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
        .toUpperCase(Locale.getDefault())

fun OffsetDateTime.displayDate(): String =
    format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))


fun Month.displayShort(): String = getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
fun Month.displayFull(): String = getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
fun LocalDate.displayDate(): String = format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))

fun displayDateRange(start: OffsetDateTime, end: OffsetDateTime): String {
    return when {
        start.year != end.year -> "${start.month.displayShort()} ${start.dayOfMonth}, ${start.year} - ${end.month.displayShort()} ${end.dayOfMonth}, ${end.year}"
        start.month == end.month -> "${start.month.displayFull()} ${start.dayOfMonth} - ${end.dayOfMonth}, ${start.year}"
        else -> "${start.month.displayShort()} ${start.dayOfMonth} - ${end.month.displayShort()} ${end.dayOfMonth}, ${end.year}"
    }
}

// Server Formatters
fun LocalDate.serverFormat(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)
fun OffsetDateTime.serverFormat(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)

// parsers
fun String.parseDateArg(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

// Transformers
fun Calendar.toLocalDate(): LocalDate = toLocalDateTime().toLocalDate()
fun Calendar.toLocalTime(): LocalTime = toLocalDateTime().toLocalTime()
fun Calendar.toLocalDateTime(): LocalDateTime = LocalDateTime.of(
    get(Calendar.YEAR),
    get(Calendar.MONTH) + 1,
    get(Calendar.DAY_OF_MONTH),
    get(Calendar.HOUR_OF_DAY),
    get(Calendar.MINUTE),
    get(Calendar.SECOND))

fun OffsetDateTime.toProto(): String = format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)


fun String.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)


fun LocalTime.toCalendar(): Calendar = atDate(LocalDate.now()).toCalendar()

fun LocalDateTime.toCalendar(): Calendar = Calendar.getInstance().apply {
    set(year, month.ordinal, dayOfMonth, hour, minute, second)
}

fun LocalDate.toCalendar(): Calendar = atStartOfDay().toCalendar()


// Util

fun Calendar.applyDate(year: Int, month: Int, dayOfMonth: Int) {
    set(Calendar.YEAR, year)
    set(Calendar.MONTH, month)
    set(Calendar.DAY_OF_MONTH, dayOfMonth)
}

fun LocalDate.minusDaysToDayOfWeek(targetDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY): LocalDate {
    //conversion so that Sunday is 0, Monday is 1, etc:
    val diffDays = (dayOfWeek.value % 7) - (targetDayOfWeek.value % 7)
    return when {
        diffDays == 0 -> this
        diffDays < 0 -> minusDays((7 + diffDays).toLong())
        else -> minusDays(diffDays.toLong())
    }
}

fun LocalDate.plusDaysToDayOfWeek(targetDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY): LocalDate {
    val diffDays = (targetDayOfWeek.value % 7) - (dayOfWeek.value % 7)
    return when {
        diffDays == 0 -> this
        diffDays < 0 -> plusDays((7 + diffDays).toLong())
        else -> plusDays(diffDays.toLong())
    }
}

fun LocalDate.dayCountInclusive(endDate: LocalDate): Int =
    (ChronoUnit.DAYS.between(this, endDate) + 1).toInt()

fun LocalDate.rangeUntil(endDate: LocalDate): List<LocalDate> {
    val days = this.until(endDate, ChronoUnit.DAYS)
    return (0..days).map { plusDays(it) }
}

fun LocalDate.atStartOfMonth(): LocalDate = withDayOfMonth(1)
fun LocalDate.atEndOfMonth(): LocalDate = withDayOfMonth(lengthOfMonth())
val LocalDate.yearMonth: YearMonth get() = YearMonth.of(year, month)

val YearWeek.days: List<LocalDate> get() = List(7) { atDay(DayOfWeek.of(it + 1)) }

val Temporal.epochDay: Long get() = ChronoUnit.DAYS.between(epoch, this) + 1
val Temporal.epochWeek: Long
    get() = ChronoUnit.WEEKS.between(YearWeek.from(epoch),
        YearWeek.from(this)) + 1

fun Temporal.isSameDay(other: Temporal): Boolean {
    if (!isSupported(ChronoField.YEAR) || !isSupported(ChronoField.DAY_OF_YEAR)) return false
    if (!other.isSupported(ChronoField.YEAR) || !other.isSupported(ChronoField.DAY_OF_YEAR)) return false

    return get(ChronoField.YEAR) == other.get(ChronoField.YEAR) && get(ChronoField.DAY_OF_YEAR) == other.get(
        ChronoField.DAY_OF_YEAR)
}

val Temporal.epochMonth: Long
    get() = ChronoUnit.MONTHS.between(YearMonth.from(epoch),
        YearMonth.from(this)) + 1


fun DayOfWeek.offset(offset: Int): DayOfWeek = plus(offset.toLong())
fun DayOfWeek.daysAfter(target: DayOfWeek): Int =
    (value - target.value).let { if (it < 0) it + 7 else it }

fun DayOfWeek.dayBefore(): DayOfWeek {
    return when (value - 1) {
        0 -> DayOfWeek.SUNDAY
        else -> DayOfWeek.of(value - 1)
    }
}

val YearMonth.next: YearMonth get() = this.plusMonths(1)
val YearMonth.previous: YearMonth get() = this.minusMonths(1)
fun YearMonth.monthRange(dayOfWeek: DayOfWeek): Range<LocalDate> {
    val startDate = atDay(1)
    val offset = startDate.dayOfWeek.daysAfter(dayOfWeek).toLong()
    val start = startDate.minusDays(offset)
    val end = start.plusDays(42)
    return Range.create(start, end)
}

fun <T> min(date1: T, date2: T): T where T : Comparable<T>, T : Temporal {
    return when {
        date1 < date2 -> date1
        date1 > date2 -> date2
        else -> date1
    }
}

object DateUtils {
    fun parseOffsetDateTime(dateTime: String): OffsetDateTime? {
        try {
            return try {
                OffsetDateTime.parse(dateTime)
            } catch (e: Exception) {
                LocalDateTime.parse(dateTime).atOffset(ZoneOffset.UTC)
            }
        } catch (e: Exception) {
        }
        return null
    }

    fun parseLocalDate(dateTime: String): LocalDate? {
        listOf(
            DateTimeFormatter.ISO_DATE_TIME,

            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE
        ).forEach { format ->
            try {
                return LocalDate.parse(dateTime, format)
            } catch (e: Exception) {
            }
        }
        return null
    }

    fun parseLocalDateTime(dateTime: String): LocalDateTime? {
        listOf(
            DateTimeFormatter.ISO_DATE_TIME,

            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ).forEach { format ->
            try {
                return LocalDateTime.parse(dateTime, format)
            } catch (e: Exception) {
            }
        }
        return null
    }

    fun parseServerDate(date: String): LocalDate {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
    }
}