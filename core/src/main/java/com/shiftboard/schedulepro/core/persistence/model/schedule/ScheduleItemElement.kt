package com.shiftboard.schedulepro.core.persistence.model.schedule

import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


data class ScheduleItemElement(
    var startOfDay: Boolean,
    val date: LocalDate,
    val type: ScheduleElementType,
    val startTime: OffsetDateTime?,
    val scheduleItem: ScheduleItem?
)