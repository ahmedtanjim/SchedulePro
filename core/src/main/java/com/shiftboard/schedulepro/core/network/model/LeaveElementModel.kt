package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LeaveElementModel(
    val id: String,
    val code: String,
    val description: String,
    val isActive: Boolean
)