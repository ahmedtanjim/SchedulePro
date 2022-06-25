package com.shiftboard.schedulepro.content.schedule.ui

import com.shiftboard.schedulepro.core.common.utils.Event
import org.threeten.bp.LocalDate


sealed class DateRangeState {
    object NotInitialized : DateRangeState()

    data class Content(val data: SummaryDateRange) : DateRangeState()
}

data class SummaryDateRange(val start: LocalDate, val end: LocalDate)

sealed class SchedulePagingState {
    object NotInitialized : SchedulePagingState()

    data class Init(val referenceDate: LocalDate) : SchedulePagingState()

    data class Error(val referenceDate: LocalDate, val throwable: Throwable?) :
        SchedulePagingState()

    data class Content(
        val referenceDate: LocalDate,
        val startOffset: LocalDate,
        val endOffset: LocalDate,
    ) : SchedulePagingState()
}

sealed class LocalDateState {
    object NotInitialized : LocalDateState()

    data class Content(val data: Event<LocalDateSelect>): LocalDateState()
}

data class LocalDateSelect(
    val localDate: LocalDate,
    val scrollRecycler: Boolean,
)