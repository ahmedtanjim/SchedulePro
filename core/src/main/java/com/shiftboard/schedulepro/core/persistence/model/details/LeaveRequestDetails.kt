package com.shiftboard.schedulepro.core.persistence.model.details

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shiftboard.schedulepro.core.persistence.Cached
import org.threeten.bp.OffsetDateTime

@Entity
data class LeaveRequestDetails(
    @PrimaryKey val id: String,
    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,
    val color: Int,
    val leaveRequestStatus: String,
    val status: String,
    val type: String,
    val startTime: OffsetDateTime?,
    val endTime: OffsetDateTime?,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val requestedTimeOff: Float,
    val requesterComments: String?,
    val managerComments: String?,

    val actions: List<String>,

    override val cachedAt: OffsetDateTime,

    // These will be null unless there is an amendment
    val amendmentStartDate: OffsetDateTime?,
    val amendmentEndDate: OffsetDateTime?,
    val amendmentRequesterComments: String?,
    val amendmentRequestedTimeOff: Float?,
) : Cached