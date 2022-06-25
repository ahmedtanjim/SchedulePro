package com.shiftboard.schedulepro.core.network.model.details


import android.graphics.Color
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.details.Leave
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveDetails
import com.shiftboard.schedulepro.core.persistence.model.details.ShiftDetails
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class LeaveDetailsModel(
    val id: String,
    val hoursPaid: Float,
    val regularOvertimeHours: Float,
    val overtimeMultiplier: Float,
    val overtimeHours: Float,
    val comments: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val positionId: String,
    val positionCode: String,
    val positionDescription: String,
    val locationId: String,
    val locationCode: String,
    val locationDescription: String,
    val color: String,
    val type: String,
    val date: LocalDate,
    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,
    val leaveSpansAllDay: Boolean,
    val leaveRequest: LeaveRequestDetailModel?,
) : Mappable<Leave> {
    override fun map(cachedAt: OffsetDateTime): Leave {
        return Leave(
            LeaveDetails(
                id = id,
                hoursPaid = hoursPaid,
                regularOvertimeHours = regularOvertimeHours,
                overtimeMultiplier = overtimeMultiplier,
                overtimeHours = overtimeHours,
                comments = comments,
                startTime = startTime,
                endTime = endTime,
                positionId = positionId,
                positionCode = positionCode,
                positionDescription = positionDescription,
                locationId = locationId,
                locationCode = locationCode,
                locationDescription = locationDescription,
                color = parseAsColor(color),
                date = date,
                typeId = leaveTypeId,
                typeCode = leaveTypeCode,
                typeDescription = leaveTypeDescription,
                leaveRequestDetailId = leaveRequest?.id,
                cachedAt = cachedAt
            ),
            leaveRequest?.map(cachedAt))
    }
}