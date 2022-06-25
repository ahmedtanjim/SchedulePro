package com.shiftboard.schedulepro.content.profile.unavailability.viewmodels

import androidx.lifecycle.ViewModel
import com.shiftboard.schedulepro.core.network.model.profile.Recurrence
import com.shiftboard.schedulepro.core.network.model.profile.Unavailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SharedViewModel() : ViewModel() {
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    fun changeIsEditing(isEditing: Boolean) {
        _isEditing.update { isEditing }
    }

    private val _recurrence = MutableStateFlow(false)
    val recurrence: StateFlow<Boolean> = _recurrence

    fun changeRecurrence(recurrence: Boolean) {
        _recurrence.update { recurrence }
    }

    private val _selectedRecurrence = MutableStateFlow<Recurrence?>(null)
    val selectedRecurrence: StateFlow<Recurrence?> = _selectedRecurrence

    fun changeSelectedRecurrence(recurrence: Recurrence?) {
        _selectedRecurrence.value = recurrence
    }

    private val _selectedUnavailability = MutableStateFlow<Unavailability?>(null)
    val selectedUnavailability: StateFlow<Unavailability?> = _selectedUnavailability

    fun changeSelectedUnavailability(unavailability: Unavailability?) {
        _selectedUnavailability.value = unavailability
    }

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex

    fun changeTabIndex(tabIndex: Int) {
        _tabIndex.update { tabIndex }
    }


}