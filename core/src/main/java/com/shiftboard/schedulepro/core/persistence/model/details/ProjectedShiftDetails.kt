package com.shiftboard.schedulepro.core.persistence.model.details

import androidx.room.Entity
import com.shiftboard.schedulepro.core.persistence.Cached
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(
    primaryKeys = ["date", "shiftTimeId"]
)
data class ProjectedShiftDetails(
    val hoursPaid: Float,
    val overtimeMultiplier: Float,
    val overtimeHours: Float,
    val regularOvertimeHours: Float,
    val date: LocalDate,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeId: String,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    val startsOnPreviousDay: Boolean,
    val startsOnNextDay: Boolean,
    val endsOnPreviousDay: Boolean,
    val endsOnNextDay: Boolean,
    val positionId: String,
    val positionCode: String,
    val positionDescription: String,
    val locationId: String,
    val locationCode: String,
    val locationDescription: String,
    val color: Int,
    val actions: List<String>,
    override val cachedAt: OffsetDateTime,
) : Cached