package com.shiftboard.schedulepro.content.details.request

import com.shiftboard.schedulepro.activities.root.InvalidationEvent
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveRequestDetails
import org.threeten.bp.LocalDate

sealed class RequestDetailState {
    data class ErrorState(val throwable: Throwable?, val requestDetails: LeaveRequestDetails? = null): RequestDetailState()
    data class LoadingState(val requestDetails: LeaveRequestDetails? = null): RequestDetailState()
    data class MainState(
        val requestDetails: LeaveRequestDetails,

        val runningCancel: Boolean = false,
        val runningDelete: Boolean = false,

        val messageEvent: Event<String>? = null,
        val cancelSuccess: Event<InvalidationEvent>? = null
    ): RequestDetailState()

    data class DeleteSuccessState(val invalidateStart: LocalDate, val invalidateEnd: LocalDate): RequestDetailState()
}
