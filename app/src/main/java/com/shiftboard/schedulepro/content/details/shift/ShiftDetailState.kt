package com.shiftboard.schedulepro.content.details.shift

import com.shiftboard.schedulepro.content.details.repo.PermissionShiftDetails


sealed class ShiftDetailState {
    data class ErrorState(val throwable: Throwable?, val shiftDetails: PermissionShiftDetails? = null): ShiftDetailState()
    data class LoadingState(val shiftDetails: PermissionShiftDetails? = null): ShiftDetailState()
    data class MainState(val shiftDetails: PermissionShiftDetails): ShiftDetailState()
}