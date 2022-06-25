package com.shiftboard.schedulepro.core.network.model.details

import android.graphics.Color
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.details.ProjectedLeaveDetails
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class ProjectedLeaveModel(
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
    val color: String
) : Mappable<ProjectedLeaveDetails> {
    override fun map(cachedAt: OffsetDateTime): ProjectedLeaveDetails {
        return ProjectedLeaveDetails(
            positionId = positionId,
            positionCode = positionCode,
            positionDescription = positionDescription,
            locationId = locationId,
            locationCode = locationCode,
            locationDescription = locationDescription,
            hoursPaid = hoursPaid,
            regularOvertimeHours = regularOvertimeHours,
            overtimeMultiplier = overtimeMultiplier,
            overtimeHours = overtimeHours,
            date = date,
            startTime = startTime,
            endTime = endTime,
            leaveSpansAllDay = leaveSpansAllDay,
            leaveTypeId = leaveTypeId,
            leaveTypeCode = leaveTypeCode,
            leaveTypeDescription = leaveTypeDescription,
            color = parseAsColor(color),
            cachedAt = cachedAt,
        )
    }
}