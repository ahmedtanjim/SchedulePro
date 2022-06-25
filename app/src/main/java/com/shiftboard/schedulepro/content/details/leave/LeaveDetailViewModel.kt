package com.shiftboard.schedulepro.content.details.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.activities.root.InvalidationEvent
import com.shiftboard.schedulepro.content.details.repo.DetailsRepo
import com.shiftboard.schedulepro.core.common.InvalidStateException
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LeaveDetailViewModel(private val detailsRepo: DetailsRepo): ViewModel() {
    val leaveDetailState = MutableStateFlow<LeaveDetailState>(LeaveDetailState.LoadingState())

    private var mainRequestSupervisor: Job? = null
    fun setLeaveId(leaveId: String) {
        mainRequestSupervisor?.cancel()
        mainRequestSupervisor = viewModelScope.launch {
            detailsRepo.getLeaveById(leaveId)
                .collect {
                    leaveDetailState.value = when (it) {
                        is CacheResponse.Success -> LeaveDetailState.MainState(it.data)
                        is CacheResponse.Loading -> LeaveDetailState.LoadingState(it.data)
                        is CacheResponse.Failure -> LeaveDetailState.ErrorState(it.exception, it.data)
                    }
                }
        }
    }

    fun requestCancellation(requestId: String) {
        runInMainState {
            viewModelScope.launch {
                leaveDetailState.value = LeaveDetailState.MainState(it.leaveDetails, runningCancel = true)

                when (val results = detailsRepo.postRequestCancellation(requestId)) {
                    is Maybe.Failure -> {
                        leaveDetailState.value = LeaveDetailState.MainState(it.leaveDetails,
                            messageEvent = Event(results.exception?.message ?: "Unknown Error"))
                    }
                    is Maybe.Success -> {
                        val successEvent = it.leaveDetails.leaveRequest?.let {
                            Event(InvalidationEvent(it.startDate.toLocalDate(), it.endDate.toLocalDate()))
                        }
                        leaveDetailState.value = LeaveDetailState.MainState(
                            it.leaveDetails.copy(leaveRequest = results.data),
                            cancelSuccess = successEvent
                        )
                    }
                }
            }
        }
    }

    fun requestDelete(requestId: String) {
        runInMainState {
            viewModelScope.launch {
                leaveDetailState.value = LeaveDetailState.MainState(it.leaveDetails, runningDelete = true)
                when (val results = detailsRepo.postRequestDelete(requestId)) {
                    is Maybe.Failure -> {
                        leaveDetailState.value = LeaveDetailState.MainState(it.leaveDetails, messageEvent = Event(results.exception?.message ?: "Unknown Error"))
                    }
                    is Maybe.Success -> {
                        leaveDetailState.value = LeaveDetailState.DeleteSuccessState
                    }
                }
            }
        }
    }

    private fun runInMainState(block: (LeaveDetailState.MainState)->Unit) {
        (leaveDetailState.value as? LeaveDetailState.MainState)?.let {
            block(it)
        } ?: run {
            leaveDetailState.value = LeaveDetailState.ErrorState(InvalidStateException("Page is not loaded."))
        }
    }
}