package com.shiftboard.schedulepro.core.network.model.profile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnavailabilityResponse(
    val unavailabilities: List<Unavailability>
)