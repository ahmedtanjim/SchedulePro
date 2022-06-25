package com.shiftboard.schedulepro.core.network.model.profile

import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@JsonClass(generateAdapter = true)
data class Unavailability(
    val employeeId: String,
    val endTime: String,
    val id: String? = null,
    val isAllDay: Boolean,
    val startTime: String,
    val subject: String,
    val unavailabilityRecurrenceId: String? = null
)
