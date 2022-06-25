package com.shiftboard.schedulepro.content.openShifts

import org.threeten.bp.LocalDate

sealed class OpenShiftRequestState {
    object LoadingState: OpenShiftRequestState()
    data class ErrorState(val exception: Throwable?): OpenShiftRequestState()
    data class SuccessState(val date: LocalDate, val isCancel: Boolean): OpenShiftRequestState()
}

