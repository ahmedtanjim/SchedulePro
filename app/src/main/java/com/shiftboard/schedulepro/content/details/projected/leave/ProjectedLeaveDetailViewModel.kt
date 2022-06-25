package com.shiftboard.schedulepro.content.details.projected.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.content.details.repo.DetailsRepo
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class ProjectedLeaveDetailViewModel(private val detailsRepo: DetailsRepo): ViewModel() {
    private val _leaveDetailState = MutableStateFlow<ProjectedLeaveDetailState>(ProjectedLeaveDetailState.IdleState)
    val leaveDetailState = _leaveDetailState.asStateFlow()

    fun setLeaveId(date: LocalDate, leaveId: String) {
        viewModelScope.launch {
            detailsRepo.getProjectedLeave(date, leaveId).collectLatest {
                    _leaveDetailState.value = when (it) {
                        is CacheResponse.Success -> ProjectedLeaveDetailState.MainState(it.data)
                        is CacheResponse.Loading -> ProjectedLeaveDetailState.LoadingState(it.data)
                        is CacheResponse.Failure -> ProjectedLeaveDetailState.ErrorState(it.exception, it.data)
                    }
                }
        }
    }
}