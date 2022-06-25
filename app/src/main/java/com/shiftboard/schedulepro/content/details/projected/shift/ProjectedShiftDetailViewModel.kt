package com.shiftboard.schedulepro.content.details.projected.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.content.details.repo.DetailsRepo
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class ProjectedShiftDetailViewModel(private val detailsRepo: DetailsRepo): ViewModel() {
    private val _shiftDetails =  MutableStateFlow<ProjectedShiftDetailState>(ProjectedShiftDetailState.IdleState)
    val shiftDetails = _shiftDetails.asStateFlow()

    fun setShiftId(date: LocalDate, shiftId: String) {
        viewModelScope.launch {
            detailsRepo.getProjectedShift(date, shiftId).collectLatest {
                when (it) {
                    is CacheResponse.Success -> _shiftDetails.value = ProjectedShiftDetailState.MainState(it.data)
                    is CacheResponse.Loading -> _shiftDetails.value = ProjectedShiftDetailState.LoadingState(it.data)
                    is CacheResponse.Failure -> _shiftDetails.value = ProjectedShiftDetailState.ErrorState(it.exception, it.data)
                }
            }
        }
    }
}