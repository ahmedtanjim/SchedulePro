package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Organization(
    val id: String,
    val name: String,
    val allowMobileAccess: Boolean,
    val is24HourClockTime: Boolean = false
)