package com.shiftboard.schedulepro.content.details.leave

import com.shiftboard.schedulepro.activities.root.InvalidationEvent
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.persistence.model.details.Leave


sealed class LeaveDetailState {
    data class ErrorState(val throwable: Throwable?, val leaveDetails: Leave? = null) :
        LeaveDetailState()

    data class LoadingState(val leaveDetails: Leave? = null) : LeaveDetailState()
    data class MainState(
        val leaveDetails: Leave,

        val runningCancel: Boolean = false,
        val runningDelete: Boolean = false,

        val messageEvent: Event<String>? = null,
        val cancelSuccess: Event<InvalidationEvent>? = null
    ) : LeaveDetailState()

    object DeleteSuccessState : LeaveDetailState()
}