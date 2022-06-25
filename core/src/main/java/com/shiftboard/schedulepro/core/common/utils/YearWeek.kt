@file:Suppress("unused")
package com.shiftboard.schedulepro.core.common.utils

import org.threeten.bp.Clock
import org.threeten.bp.DateTimeException
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Year
import org.threeten.bp.ZoneId
import org.threeten.bp.chrono.Chronology
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.SignStyle
import org.threeten.bp.jdk8.DefaultInterfaceTemporalAccessor
import org.threeten.bp.jdk8.Jdk8Methods
import org.threeten.bp.temporal.ChronoField
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.IsoFields.WEEK_BASED_YEAR
import org.threeten.bp.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR
import org.threeten.bp.temporal.Temporal
import org.threeten.bp.temporal.TemporalAccessor
import org.threeten.bp.temporal.TemporalAdjuster
import org.threeten.bp.temporal.TemporalAmount
import org.threeten.bp.temporal.TemporalField
import org.threeten.bp.temporal.TemporalQueries
import org.threeten.bp.temporal.TemporalQuery
import org.threeten.bp.temporal.TemporalUnit
import org.threeten.bp.temporal.UnsupportedTemporalTypeException
import org.threeten.bp.temporal.ValueRange
import kotlin.math.abs

class YearWeek private constructor(weekBasedYear: Int, week: Int) : DefaultInterfaceTemporalAccessor(), Temporal, TemporalAdjuster, Comparable<YearWeek> {

    var year: Int = weekBasedYear
        private set
    var week: Int = week
        private set


    private fun with(newYear: Int, newWeek: Int): YearWeek {
        return if (year == newYear && week == newWeek) {
            this
        } else {
            of(newYear, newWeek)
        }
    }

    override fun isSupported(field: TemporalField): Boolean {
        return if (field == WEEK_OF_WEEK_BASED_YEAR || field == WEEK_BASED_YEAR) {
            true
        } else if (field is ChronoField) {
            false
        } else {
            field.isSupportedBy(this)
        }
    }

    override fun range(field: TemporalField?): ValueRange {
        return when (field) {
            WEEK_BASED_YEAR -> WEEK_BASED_YEAR.range()
            WEEK_OF_WEEK_BASED_YEAR -> ValueRange.of(1, weekRange(year).toLong())
            else -> super.range(field)
        }
    }

    override fun get(field: TemporalField): Int {
        return when (field) {
            WEEK_BASED_YEAR -> year
            WEEK_OF_WEEK_BASED_YEAR -> week
            else -> super.get(field)
        }
    }

    override fun getLong(field: TemporalField): Long {
        return when (field) {
            WEEK_BASED_YEAR -> year.toLong()
            WEEK_OF_WEEK_BASED_YEAR -> week.toLong()
            is ChronoField -> throw UnsupportedTemporalTypeException("Unsupported field: $field")
            else -> field.getFrom(this)
        }
    }

    fun is53WeekYear(): Boolean = weekRange(year) == 53
    fun lengthOfYear(): Int = if (is53WeekYear()) 371 else 364

    fun withYear(weekBasedYear: Int): YearWeek {
        if (week == 53 && weekRange(weekBasedYear) < 53) {
            return of(weekBasedYear, 52)
        }
        return with(weekBasedYear, week)
    }


    fun withWeek(week: Int): YearWeek = with(year, week)

    fun plusYears(yearsToAdd: Long): YearWeek {
        if (yearsToAdd == 0L) return this
        val newYear = year + yearsToAdd
        return withYear(newYear.toInt())
    }

    fun plusYears(yearsToAdd: Int): YearWeek = plusYears(yearsToAdd.toLong())


    fun minusYears(yearsToSubtract: Long): YearWeek {
        if (yearsToSubtract == 0L) return this
        val newYear = year - yearsToSubtract
        return withYear(newYear.toInt())
    }

    fun minusWeeks(weeksToSubtract: Long): YearWeek {
        if (weeksToSubtract == 0L) {
            return this
        }
        val mondayOfWeek = atDay(DayOfWeek.MONDAY).minusWeeks(weeksToSubtract)
        return from(mondayOfWeek)
    }

    fun plusWeeks(weeksToAdd: Long): YearWeek {
        if (weeksToAdd == 0L) {
            return this
        }
        val mondayOfWeek = atDay(DayOfWeek.MONDAY).plusWeeks(weeksToAdd)
        return from(mondayOfWeek)
    }

    override fun <R> query(query: TemporalQuery<R>): R {
        return if (query === TemporalQueries.chronology()) {
            IsoChronology.INSTANCE as R
        } else {
            super.query(query)
        }
    }

    override fun adjustInto(temporal: Temporal): Temporal {
        if (Chronology.from(temporal) != IsoChronology.INSTANCE) {
            throw DateTimeException("Adjustment only supported on ISO date-time")
        }
        return temporal.with(WEEK_BASED_YEAR, year.toLong()).with(WEEK_OF_WEEK_BASED_YEAR, week.toLong())
    }

    fun format(formatter: DateTimeFormatter): String = formatter.format(this)

    fun atDay(dayOfWeek: DayOfWeek): LocalDate {
        val correction = LocalDate.of(year, 1, 4).dayOfWeek.value + 3
        val dayOfYear = week * 7 + dayOfWeek.value - correction
        val maxDaysOfYear = if(Year.isLeap(year.toLong())) 366 else 365
        if (dayOfYear > maxDaysOfYear) {
            return LocalDate.ofYearDay(year + 1, dayOfYear - maxDaysOfYear)
        }
        if (dayOfYear > 0) {
            return LocalDate.ofYearDay(year, dayOfYear)
        } else {
            val daysOfPreviousYear = if (Year.isLeap((year - 1).toLong())) 366 else 365
            return LocalDate.ofYearDay(year - 1, daysOfPreviousYear + dayOfYear)
        }
    }

    override fun compareTo(other: YearWeek): Int {
        var cmp = year - other.year
        if (cmp == 0) {
            cmp = week - other.week
        }
        return cmp
    }

    fun isAfter(other: YearWeek): Boolean = compareTo(other) > 0
    fun isBefore(other: YearWeek): Boolean = compareTo(other) < 0

    override fun equals(other: Any?): Boolean =
        if (other is YearWeek) year == other.year && week == other.week
        else false


    override fun hashCode(): Int = year xor (week shl 25)

    override fun with(p0: TemporalAdjuster): Temporal {
        return p0.adjustInto(this)
    }

    override fun with(field: TemporalField, newValue: Long): YearWeek? {
        return if (field is ChronoField) {
            field.checkValidValue(newValue)
            when (field) {
                ChronoField.ALIGNED_WEEK_OF_YEAR -> withWeek(newValue.toInt())
                ChronoField.YEAR_OF_ERA -> withYear((if (year < 1) 1L - newValue else newValue).toInt())
                ChronoField.YEAR -> withYear(newValue.toInt())
                ChronoField.ERA -> if (getLong(ChronoField.ERA) == newValue) this else withYear(1 - year)
                else -> throw UnsupportedTemporalTypeException("Unsupported field: $field")
            }
        } else {
            field.adjustInto(this, newValue) as YearWeek
        }
    }

    override fun isSupported(unit: TemporalUnit): Boolean {
        return if (unit is ChronoUnit) {
            unit === ChronoUnit.YEARS || unit === ChronoUnit.WEEKS
        } else {
            unit != null && unit.isSupportedBy(this)
        }
    }

    override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
        val end: YearWeek = from(endExclusive)
        return atDay(DayOfWeek.MONDAY).until(end.atDay(DayOfWeek.MONDAY), unit)
    }

    override fun plus(p0: TemporalAmount): Temporal = p0.addTo(this)
    override fun minus(p0: TemporalAmount): Temporal = p0.subtractFrom(this)

    override fun plus(amount: Long, unit: TemporalUnit): Temporal {
        return if (unit is ChronoUnit) {
            when (unit) {
                ChronoUnit.WEEKS -> plusWeeks(amount)
                ChronoUnit.YEARS -> plusYears(amount)
                ChronoUnit.DECADES -> plusYears(Jdk8Methods.safeMultiply(amount, 10))
                ChronoUnit.CENTURIES -> plusYears(Jdk8Methods.safeMultiply(amount, 100))
                ChronoUnit.MILLENNIA -> plusYears(Jdk8Methods.safeMultiply(amount, 1000))
                else -> throw UnsupportedTemporalTypeException("Unsupported unit: $unit")
            }
        } else {
            (unit.addTo(this, amount) as YearWeek)
        }
    }

    override fun minus(amount: Long, unit: TemporalUnit): Temporal {
        return if (unit is ChronoUnit) {
            when (unit) {
                ChronoUnit.WEEKS -> minusWeeks(amount)
                ChronoUnit.YEARS -> minusYears(amount)
                ChronoUnit.DECADES -> minusYears(Jdk8Methods.safeMultiply(amount, 10))
                ChronoUnit.CENTURIES -> minusYears(Jdk8Methods.safeMultiply(amount, 100))
                ChronoUnit.MILLENNIA -> minusYears(Jdk8Methods.safeMultiply(amount, 1000))
                else -> throw UnsupportedTemporalTypeException("Unsupported unit: $unit")
            }
        } else {
            (unit.addTo(this, amount) as YearWeek)
        }
    }

    override fun toString(): String {
        val absYear = abs(year)
        val buf = StringBuilder(10)
        if (absYear < 1000) {
            if (year < 0) {
                buf.append(year - 10000).deleteCharAt(1)
            } else {
                buf.append(year + 10000).deleteCharAt(0)
            }
        } else {
            if (year > 9999) {
                buf.append('+')
            }
            buf.append(year)
        }
        return buf.append(if (week < 10) "-W0" else "-W").append(week).toString()
    }

    companion object {
        private val serialVersionUID = 3381384054273223921L
        private val PARSER: DateTimeFormatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(WEEK_BASED_YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral("-W")
            .appendValue(WEEK_OF_WEEK_BASED_YEAR, 2)
            .toFormatter();

        fun now(): YearWeek = now(Clock.systemDefaultZone())
        fun now(zoneId: ZoneId): YearWeek = now(Clock.system(zoneId))
        fun now(clock: Clock): YearWeek {
            val now = LocalDate.now(clock)
            return of(now.get(WEEK_BASED_YEAR), now.get(WEEK_OF_WEEK_BASED_YEAR))
        }

        fun nowOffset(dayOfWeek: DayOfWeek): YearWeek {
            val today = LocalDate.now()
            val week = now()
            val startOfWeek = week.atDay(dayOfWeek)
            return if (startOfWeek.isAfter(today)) week.minusWeeks(1) else week
        }


        fun fromOffset(dayOfWeek: DayOfWeek, date: LocalDate): YearWeek {
            val week = from(date)
            val startOfWeek = week.atDay(dayOfWeek)

            return if (startOfWeek.isAfter(date))
                week.minusWeeks(1) else week
        }

        fun of(weekBasedYear: Int, week: Int): YearWeek {
            var _week = week
            var _weekBasedYear = weekBasedYear

            WEEK_BASED_YEAR.range().checkValidValue(_weekBasedYear.toLong(), WEEK_BASED_YEAR)
            WEEK_OF_WEEK_BASED_YEAR.range().checkValidValue(_week.toLong(), WEEK_OF_WEEK_BASED_YEAR)
            if (_week == 53 && weekRange(_weekBasedYear) < 53) {
                _week = 1
                _weekBasedYear++
                WEEK_BASED_YEAR.range().checkValidValue(_weekBasedYear.toLong(), WEEK_BASED_YEAR)
            }
            return YearWeek(_weekBasedYear, _week)
        }

        private fun weekRange(weekBasedYear: Int): Int {
            val date = LocalDate.of(weekBasedYear, 1, 1)
            if (date.dayOfWeek == DayOfWeek.THURSDAY || (date.dayOfWeek == DayOfWeek.WEDNESDAY && date.isLeapYear)) {
                return 53
            }
            return 52
        }

        fun from(temporal: TemporalAccessor): YearWeek {
            if (temporal is YearWeek) {
                return temporal
            }
            var temp = temporal
            try {
                if (IsoChronology.INSTANCE != Chronology.from(temporal)) {
                    temp = LocalDate.from(temporal)
                }
                val year = temp.getLong(WEEK_BASED_YEAR).toInt()
                val week = temp.getLong(WEEK_OF_WEEK_BASED_YEAR).toInt()
                return of(year, week)
            } catch (e: DateTimeException) {
                throw DateTimeException("Unable to obtain YearWeek from TemporalAccessor: $temporal of type ${temporal::class.simpleName}")
            }
        }

        fun parse(text: CharSequence): YearWeek {
            return parse(text, PARSER)
        }

        fun parse(text: CharSequence, formatter: DateTimeFormatter): YearWeek {
            return formatter.parse(text, Companion::from)
        }
    }
}