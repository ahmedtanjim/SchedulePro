package com.shiftboard.schedulepro.content.leaves.request

import androidx.lifecycle.*
import com.shiftboard.schedulepro.content.leaves.repo.LeaveRepo
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.LeaveElementModel
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.launch

class LeaveRequestViewModel(private val leaveRepo: LeaveRepo) : ViewModel() {
    val leaveTypes: LiveData<CacheResponse<List<LeaveElementModel>>> by lazy {
        liveData<CacheResponse<List<LeaveElementModel>>>(viewModelScope.coroutineContext) {
            emit(CacheResponse.Loading())
            when (val results = leaveRepo.fetchLeaveList()) {
                is Maybe.Success -> emit(CacheResponse.Success(results.data))
                is Maybe.Failure -> emit(CacheResponse.Failure(results.exception))
            }
        }
    }

    private val _submitState = MutableLiveData<Event<LeaveRequestState>>()
    val submitState: LiveData<Event<LeaveRequestState>> = _submitState

    fun submitLeaveRequest(event: PostLeaveRequest) {
        viewModelScope.launch {
            _submitState.postValue(Event(LeaveRequestState.LoadingState))

            when (val results = leaveRepo.submitLeaveRequest(event)) {
                is Maybe.Success -> {
                    _submitState.postValue(Event(LeaveRequestState.SuccessState(event.startDate, event.endDate)))
                }
                is Maybe.Failure -> {
                    _submitState.postValue(Event(LeaveRequestState.ErrorState(results.exception)))
                }
            }
        }
    }
}