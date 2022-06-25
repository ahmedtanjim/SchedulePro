package com.shiftboard.schedulepro.core.network.model.profile

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class Availability(
    val id: Int,
    val subject: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val unavailabilityRecurrenceId: Int,
    val isAllDay: Boolean
)
