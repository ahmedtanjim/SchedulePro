package com.shiftboard.schedulepro.core.network.model.profile

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

data class TimeSlot(
    val subject: String,
    val repeatType: PatternType,
    val repeatPeriod: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val endDate: LocalDate,
    val daysOn: DaysOfWeek,
    val date: LocalDate?
)