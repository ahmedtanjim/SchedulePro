package com.shiftboard.schedulepro.content.openShifts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.openshift.OpenShiftDetailsCollection
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

import timber.log.Timber


class OpenShiftDialogViewModel(private val openShiftRepo: OpenShiftRepo) : ViewModel() {

    private val _submitState = MutableLiveData<Event<OpenShiftRequestState>>()
    private val _shiftHolder = MutableLiveData<OpenShiftDetailsCollection>()
    val submitState: LiveData<Event<OpenShiftRequestState>> = _submitState
    var selectedShift: String = ""
    var isCancel: Boolean = false
    val shifts: LiveData<OpenShiftDetailsCollection> = _shiftHolder

    fun postOpenShiftRequest(id: String, date: LocalDate) {
        submitOpenShiftRequest(id, date, false)
    }
    fun deleteOpenShiftRequest(id: String, date: LocalDate) {
        submitOpenShiftRequest(id, date, true)
    }

    fun submitOpenShiftRequest(id: String, date: LocalDate, isDelete: Boolean) {
        viewModelScope.launch {
            _submitState.postValue(Event(OpenShiftRequestState.LoadingState))
            when (val results = if (isDelete) openShiftRepo.deleteOpenShiftRequest(id) else openShiftRepo.postOpenShiftRequest(id)) {
                is Maybe.Success -> {
                    _submitState.postValue(Event(OpenShiftRequestState.SuccessState(date, isDelete)))
                }
                is Maybe.Failure -> {
                    _submitState.postValue(Event(OpenShiftRequestState.ErrorState(results.exception)))
                }
            }
        }
    }
    fun loadDate(date: LocalDate) {
        viewModelScope.launch {
            when (val results = openShiftRepo.fetchOpenShifts(date)) {
                is Maybe.Success -> {
                    _shiftHolder.postValue(results.data!!)
                }
                is Maybe.Failure -> {
                    Timber.d(results.exception)
                }
            }
        }
    }
}
