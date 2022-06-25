package com.shiftboard.schedulepro.content.leaves.request

import org.threeten.bp.LocalDate

sealed class LeaveRequestState {
    object LoadingState: LeaveRequestState()
    data class ErrorState(val exception: Throwable?): LeaveRequestState()
    data class SuccessState(val invalidateStart: LocalDate, val invalidateEnd: LocalDate): LeaveRequestState()
}

