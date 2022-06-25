package com.shiftboard.schedulepro.core.network.model.notification

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationRequest(
    val fcmToken: String,
    val deviceType: Int = 0
)