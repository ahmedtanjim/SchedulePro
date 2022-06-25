package com.shiftboard.schedulepro.core.network.model.details

import android.graphics.Color
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveRequestDetails
import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class LeaveRequestDetailModel(

    val id: String,
    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,
    val color: String,
    val leaveRequestStatus: String,
    val status: String,
    val type: String,
    val startTime: OffsetDateTime?,
    val endTime: OffsetDateTime?,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val requesterComments: String?,
    val managerComments: String?,
    val requestedTimeOff: Float,
    val actions: List<String>,
    val amendment: LeaveAmendment?
): Mappable<LeaveRequestDetails> {
    override fun map(cachedAt: OffsetDateTime): LeaveRequestDetails {
        return LeaveRequestDetails(
            id = id,
            leaveTypeId = leaveTypeId,
            leaveTypeCode = leaveTypeCode,
            leaveTypeDescription = leaveTypeDescription,
            color = parseAsColor(color),
            leaveRequestStatus = leaveRequestStatus,
            status = status,
            type = type,
            startDate = startDate,
            endDate = endDate,
            startTime = startTime,
            endTime = endTime,
            managerComments = managerComments,
            requesterComments = requesterComments,
            requestedTimeOff = requestedTimeOff,
            actions = actions,
            cachedAt = cachedAt,
            amendmentStartDate = amendment?.startDate,
            amendmentEndDate = amendment?.endDate,
            amendmentRequesterComments = amendment?.requesterComments,
            amendmentRequestedTimeOff = amendment?.requestedTimeOff
        )
    }
}

@JsonClass(generateAdapter = true)
data class LeaveAmendment(
    val startDate: OffsetDateTime?,
    val endDate: OffsetDateTime?,
    val requesterComments: String?,
    val requestedTimeOff: Float?
)