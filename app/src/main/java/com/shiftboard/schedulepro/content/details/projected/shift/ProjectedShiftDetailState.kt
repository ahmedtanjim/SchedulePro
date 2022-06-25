package com.shiftboard.schedulepro.content.details.projected.shift

import com.shiftboard.schedulepro.core.persistence.model.details.ProjectedShiftDetails

sealed class ProjectedShiftDetailState {
    object IdleState: ProjectedShiftDetailState()
    data class ErrorState(val throwable: Throwable?, val shiftDetails: ProjectedShiftDetails? = null): ProjectedShiftDetailState()
    data class LoadingState(val shiftDetails: ProjectedShiftDetails? = null): ProjectedShiftDetailState()
    data class MainState(val shiftDetails: ProjectedShiftDetails): ProjectedShiftDetailState()
}