package com.shiftboard.schedulepro.content.details.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.activities.root.InvalidationEvent
import com.shiftboard.schedulepro.content.details.repo.DetailsRepo
import com.shiftboard.schedulepro.core.common.InvalidStateException
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RequestDetailViewModel(
    private val detailsRepo: DetailsRepo
): ViewModel() {
    val requestDetailState = MutableStateFlow<RequestDetailState>(RequestDetailState.LoadingState())

    private var mainRequestSupervisor: Job? = null
    fun setRequestId(requestId: String) {
        mainRequestSupervisor?.cancel()
        mainRequestSupervisor = viewModelScope.launch {
            detailsRepo.getLeaveRequestById(requestId)
                .collect {
                    requestDetailState.value = when (it) {
                        is CacheResponse.Success -> RequestDetailState.MainState(it.data)
                        is CacheResponse.Loading -> RequestDetailState.LoadingState(it.data)
                        is CacheResponse.Failure -> RequestDetailState.ErrorState(it.exception, it.data)
                    }
                }
        }
    }

    fun requestCancellation(requestId: String) {
        runInMainState {
            viewModelScope.launch {
                requestDetailState.value =
                    RequestDetailState.MainState(it.requestDetails, runningCancel = true)
                when (val results = detailsRepo.postRequestCancellation(requestId)) {
                    is Maybe.Failure -> {
                        requestDetailState.value = RequestDetailState.MainState(it.requestDetails,
                            messageEvent = Event(results.exception?.message ?: "Unknown Error"))
                    }
                    is Maybe.Success -> {
                        requestDetailState.value =
                            RequestDetailState.MainState(
                                results.data,
                                cancelSuccess = Event(InvalidationEvent(results.data.startDate.toLocalDate(), results.data.endDate.toLocalDate()))
                            )
                    }
                }
            }
        }
    }

    fun requestDelete(requestId: String) {
        runInMainState {
            viewModelScope.launch {
                requestDetailState.value = RequestDetailState.MainState(it.requestDetails, runningDelete = true)
                when (val results = detailsRepo.postRequestDelete(requestId)) {
                    is Maybe.Failure -> {
                        requestDetailState.value = RequestDetailState.MainState(it.requestDetails, messageEvent = Event(results.exception?.message ?: "Unknown Error"))
                    }
                    is Maybe.Success -> {
                        requestDetailState.value = RequestDetailState.DeleteSuccessState(it.requestDetails.startDate.toLocalDate(), it.requestDetails.endDate.toLocalDate())
                    }
                }
            }
        }
    }

    private fun runInMainState(block: (RequestDetailState.MainState)->Unit) {
        (requestDetailState.value as? RequestDetailState.MainState)?.let {
            block(it)
        } ?: run {
            requestDetailState.value = RequestDetailState.ErrorState(InvalidStateException("Page is not loaded."))
        }
    }
}