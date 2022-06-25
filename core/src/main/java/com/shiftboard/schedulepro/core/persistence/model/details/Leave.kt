package com.shiftboard.schedulepro.core.persistence.model.details

import androidx.room.Embedded
import androidx.room.Relation
import com.shiftboard.schedulepro.core.persistence.Cached
import org.threeten.bp.OffsetDateTime

data class Leave(
    @Embedded val leave: LeaveDetails,
    @Relation(
        parentColumn="leaveRequestDetailId",
        entityColumn="id"
    )
    val leaveRequest: LeaveRequestDetails?
): Cached {
    override val cachedAt: OffsetDateTime
        get() {
            return if (leaveRequest != null && leaveRequest.cachedAt > leave.cachedAt) {
                leaveRequest.cachedAt
            } else {
                leave.cachedAt
            }
        }
}