package com.shiftboard.schedulepro.core.network.model.details

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveRequestAction(val action: String) {
    companion object {
        fun cancellationRequest() = LeaveRequestAction("RequestCancellation")
        fun deleteRequest() = LeaveRequestAction("Delete")
    }
}