package com.shiftboard.schedulepro.core.persistence.model.details

import androidx.room.Entity
import com.shiftboard.schedulepro.core.persistence.Cached
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(
    primaryKeys = ["date", "leaveTypeId"]
)
data class ProjectedLeaveDetails(

    val positionId: String,
    val positionCode: String,
    val positionDescription: String,
    val locationId: String,
    val locationCode: String,
    val locationDescription: String,
    val hoursPaid: Float,
    val regularOvertimeHours: Float,
    val overtimeMultiplier: Float,
    val overtimeHours: Float,
    val date: LocalDate,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val leaveSpansAllDay: Boolean,
    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,
    val color: Int,
    override val cachedAt: OffsetDateTime,
) : Cached