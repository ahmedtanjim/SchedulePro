package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.Entity
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


abstract class BaseShift: ScheduleItem {
    abstract val startTime: OffsetDateTime
    abstract val endTime: OffsetDateTime
    abstract val shiftTimeId: String
    abstract val shiftTimeCode: String
    abstract val shiftTimeDescription: String
    abstract val startsOnPreviousDay: Boolean
    abstract val startsOnNextDay: Boolean
    abstract val endsOnPreviousDay: Boolean
    abstract val endsOnNextDay: Boolean

    abstract val positionId: String?
    abstract val positionCode: String?
    abstract val positionDescription: String?

    abstract val locationId: String?
    abstract val locationCode: String?
    abstract val locationDescription: String?

    abstract val color: Int
    abstract val regularOvertimeHours: Float

    abstract val cachedAt: OffsetDateTime

    val startDiff: Boolean get() = startsOnNextDay || startsOnPreviousDay
    val endDiff: Boolean get() = endsOnNextDay || endsOnPreviousDay
}

@Entity(primaryKeys = ["date", "id"])
data class ShiftElement(
    val id: String,
    val comments: String,

    override val date: LocalDate,
    override val startTime: OffsetDateTime,
    override val endTime: OffsetDateTime,
    override val shiftTimeId: String,
    override val shiftTimeCode: String,
    override val shiftTimeDescription: String,
    override val startsOnPreviousDay: Boolean,
    override val startsOnNextDay: Boolean,
    override val endsOnPreviousDay: Boolean,
    override val endsOnNextDay: Boolean,
    override val positionId: String?,
    override val positionCode: String?,
    override val positionDescription: String?,
    override val locationId: String?,
    override val locationCode: String?,
    override val locationDescription: String?,
    override val color: Int,
    override val regularOvertimeHours: Float,
    override val cachedAt: OffsetDateTime
) : BaseShift()

@Entity(
    primaryKeys = ["date", "shiftTimeId"]
)
data class ProjectedShiftElement(
    val actions: List<String>,

    override val date: LocalDate,
    override val shiftTimeId: String,
    override val startTime: OffsetDateTime,
    override val endTime: OffsetDateTime,
    override val shiftTimeCode: String,
    override val shiftTimeDescription: String,
    override val positionId: String?,
    override val positionCode: String?,
    override val positionDescription: String?,
    override val locationId: String?,
    override val locationCode: String?,
    override val locationDescription: String?,
    override val color: Int,
    override val startsOnPreviousDay: Boolean,
    override val startsOnNextDay: Boolean,
    override val endsOnPreviousDay: Boolean,
    override val endsOnNextDay: Boolean,
    override val regularOvertimeHours: Float,
    override val cachedAt: OffsetDateTime,
) : BaseShift()
