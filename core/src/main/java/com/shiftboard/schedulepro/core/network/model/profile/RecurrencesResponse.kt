package com.shiftboard.schedulepro.core.network.model.profile

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecurrencesResponse(
    val unavailabilityRecurrences: List<Recurrence>
)
