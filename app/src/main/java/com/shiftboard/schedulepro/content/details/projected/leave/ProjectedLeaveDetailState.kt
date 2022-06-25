package com.shiftboard.schedulepro.content.details.projected.leave

import com.shiftboard.schedulepro.core.persistence.model.details.ProjectedLeaveDetails


sealed class ProjectedLeaveDetailState {

    object IdleState: ProjectedLeaveDetailState()

    data class ErrorState(val throwable: Throwable?, val leaveDetails: ProjectedLeaveDetails? = null) : ProjectedLeaveDetailState()
    data class LoadingState(val leaveDetails: ProjectedLeaveDetails? = null) : ProjectedLeaveDetailState()
    data class MainState(val leaveDetails: ProjectedLeaveDetails) : ProjectedLeaveDetailState()
}