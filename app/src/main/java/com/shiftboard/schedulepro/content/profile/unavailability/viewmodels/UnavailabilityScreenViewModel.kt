package com.shiftboard.schedulepro.content.profile.unavailability.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.content.profile.repo.UnavailabilityRepo
import com.shiftboard.schedulepro.content.profile.unavailability.ScreenState
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.profile.Unavailability
import com.shiftboard.schedulepro.core.network.model.profile.Recurrence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class UnavailabilityScreenViewModel(private val unavailabilityRepo: UnavailabilityRepo) : ViewModel() {
    private val _screenState = MutableStateFlow(ScreenState.UnavailabilityScreen)
    val screenState: StateFlow<ScreenState> = _screenState

    fun changeScreenState(screenState: ScreenState) {
        _screenState.update { screenState }
    }

    private val _recurrencesLoading = MutableStateFlow(false)
    val recurrencesLoading: StateFlow<Boolean> = _recurrencesLoading

    private val _unavailabilityLoading = MutableStateFlow(false)
    val unavailabilitiesLoading: StateFlow<Boolean> = _unavailabilityLoading

    private val userId = CredentialPrefs.userId


    fun getData(){
        _recurrencesLoading.value = true
        _unavailabilityLoading.value = true
        viewModelScope.launch {
            when(val result = unavailabilityRepo.getRecurrences(userId)){
                is Maybe.Success -> {
                    _recurrencesLoading.value = false
                    result.data.let {
                        _recurrences.value = it ?: listOf()
                    }
                }
                is Maybe.Failure -> {
                    _recurrencesLoading.value = false
                    Timber.tag("unavailabilityresult").d(result.exception)
                }
            }
            when(val result = unavailabilityRepo.getUnavailabilities(userId)){
                is Maybe.Success -> {
                    _unavailabilityLoading.value = false
                    result.data.let {
                        _unavailabilities.value = it
                    }
                }
                is Maybe.Failure -> {
                    _unavailabilityLoading.value = false
                    Log.d("unavailabilityresult1", "${result.exception}")
                }
            }
        }

    }

    private val _recurrences = MutableStateFlow<List<Recurrence>>(listOf())
    val recurrences: StateFlow<List<Recurrence>> = _recurrences

    private val _unavailabilities = MutableStateFlow<List<Unavailability>>(listOf())
    val unavailabilities: StateFlow<List<Unavailability>> = _unavailabilities

}