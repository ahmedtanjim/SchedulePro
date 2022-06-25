package com.shiftboard.schedulepro.core.network.model.profile

import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month

@JsonClass(generateAdapter = true)
data class Recurrence(
    val daysOfWeekBitmask: Int,
    val employeeId: String,
    val endTime: String,
    val id: String? = null,
    val isWeekdaysOnly: Boolean,
    val patternDay: Int,
    val patternEndDate: String,
    val patternInterval: Int,
    val patternStartDate: String,
    val patternType: String,
    val startTime: String,
    val subject: String
)