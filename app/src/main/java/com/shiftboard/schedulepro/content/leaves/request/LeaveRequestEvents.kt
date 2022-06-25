package com.shiftboard.schedulepro.content.leaves.request

import org.threeten.bp.LocalDate

data class PostLeaveRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val type: String,
    val notes: String
)