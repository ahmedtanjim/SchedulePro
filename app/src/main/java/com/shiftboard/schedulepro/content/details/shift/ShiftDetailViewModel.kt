package corm.shiftboard.schedulepro.content.details.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.activities.splash.repo.UserCacheRepo
import com.shiftboard.schedulepro.content.details.repo.DetailsRepo
import com.shiftboard.schedulepro.content.details.shift.ShiftDetailState
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShiftDetailViewModel(private val shiftRepo: DetailsRepo, private val userCacheRepo: UserCacheRepo): ViewModel() {
    private val shiftIdState =  MutableStateFlow("")
    val turnDownState = MutableStateFlow(false)

    val shiftDetails = shiftIdState.flatMapLatest {
        if (it.isNotBlank()) getShiftDetailState(it)
        else flowOf(ShiftDetailState.LoadingState())
    }

    suspend fun tradePermission(): Boolean {
        return userCacheRepo.permissions().trade.create
    }

    private fun getShiftDetailState(shiftId: String): Flow<ShiftDetailState> {
        return shiftRepo.getShiftById(shiftId).map {
            when (it) {
                is CacheResponse.Success -> ShiftDetailState.MainState(it.data)
                is CacheResponse.Loading -> ShiftDetailState.LoadingState(it.data)
                is CacheResponse.Failure -> ShiftDetailState.ErrorState(it.exception, it.data)
            }
        }
    }

    fun turndownShift() {
        val shiftId = shiftIdState.value
        viewModelScope.launch {
            when(shiftRepo.postTurndown(shiftId)) {
                is Maybe.Failure -> turnDownState.value = false
                is Maybe.Success -> turnDownState.value = true
            }
        }
    }

    fun cancelTurndown() {
        val shiftId = shiftIdState.value
        viewModelScope.launch {
            when(shiftRepo.cancelTurndown(shiftId)) {
                is Maybe.Failure -> turnDownState.value = false
                is Maybe.Success -> turnDownState.value = true
            }
        }
    }

    fun setShiftId(shiftId: String) {
        shiftIdState.value = shiftId
    }
}