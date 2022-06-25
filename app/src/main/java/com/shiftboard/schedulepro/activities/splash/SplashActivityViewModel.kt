package com.shiftboard.schedulepro.activities.splash

import androidx.lifecycle.*
import com.shiftboard.schedulepro.activities.splash.repo.UserCacheRepo
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.persistence.ScheduleProDB
import kotlinx.coroutines.launch

class SplashActivityViewModel(
    private val db: ScheduleProDB,
    private val userRepo: UserCacheRepo
): ViewModel() {
    private val _prepApp = MutableLiveData<Event<SplashPrepState>>()
    val prepAppState: LiveData<Event<SplashPrepState>> = _prepApp

    fun loadApp() {
        viewModelScope.launch {
            _prepApp.postValue(Event(SplashPrepState.Loading))
            db.testIntegrity()
            when(val result = userRepo.cacheUser()) {
                is Maybe.Success -> {
                    _prepApp.postValue(Event(SplashPrepState.Ready))
                    saveLogInEvent()
                }
                is Maybe.Failure -> {
                    _prepApp.postValue(Event(SplashPrepState.Error(result.exception)))
                }
            }
        }
    }

    private suspend fun saveLogInEvent() {
        userRepo.saveLogInEvent()
    }
}

sealed class SplashPrepState {
    object Loading: SplashPrepState()
    object Ready: SplashPrepState()
    data class Error(val throwable: Throwable?): SplashPrepState()
}
