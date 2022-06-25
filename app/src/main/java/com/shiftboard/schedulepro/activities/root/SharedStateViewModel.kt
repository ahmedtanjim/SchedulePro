package com.shiftboard.schedulepro.activities.root

import androidx.lifecycle.*
import com.shiftboard.schedulepro.activities.root.repo.SharedRepo
import com.shiftboard.schedulepro.activities.splash.repo.UserCacheRepo
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.common.utils.mutableLiveData
import com.shiftboard.schedulepro.core.network.common.Maybe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

class SharedStateViewModel(private val sharedRepo: SharedRepo, private val userCacheRepo: UserCacheRepo) : ViewModel() {
    // state flow for helping track events that invalidate the state of the schedule
    private val _invalidateSchedule: MutableLiveData<Event<InvalidationEvent>> = MutableLiveData<Event<InvalidationEvent>>()
    val invalidateScheduleTracker : LiveData<Event<InvalidationEvent>> = _invalidateSchedule

    private val _snackNotifications = MutableStateFlow(Event(SnackbarEvent(""), handled = true))
    val snackNotifications = _snackNotifications.asLiveData(viewModelScope.coroutineContext)

    private val _notificationCount: MutableLiveData<Int> = mutableLiveData(0)
    val notificationCount: LiveData<Int> = _notificationCount

    fun markNotificationRead(notificationId: String) {
        viewModelScope.launch {
            sharedRepo.markNotificationRead(notificationId)
        }
    }
    suspend fun groupPermission(): Boolean {
        return userCacheRepo.permissions().groupSchedule.read
    }

    fun updateNotificationCount() {
        viewModelScope.launch {
            when(val result = sharedRepo.checkNotificationCount()){
                is Maybe.Success -> {
                    _notificationCount.postValue(result.data ?: 0)
                }
                is Maybe.Failure -> { /* don't care */ }
            }
        }
    }

    fun postSnackbarNotification(message: SnackbarEvent) {
        _snackNotifications.value = Event(message)
    }

    fun invalidateSchedule(startDate: LocalDate, endDate: LocalDate) {
        _invalidateSchedule.postValue(Event(InvalidationEvent(startDate, endDate)))
    }
}

data class SnackbarEvent(val message: String, val delay: Long = 500)

data class InvalidationEvent(val startDate: LocalDate, val endDate: LocalDate)