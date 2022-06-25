package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LeaveRequest(
    val leaveTypeId: String,
    val startDate: String,
    val endDate: String,
    val comment: String?
)