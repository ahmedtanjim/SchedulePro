package com.shiftboard.schedulepro.activities.router

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.activities.root.repo.SharedRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RouterViewModel(private val sharedRepo: SharedRepo) : ViewModel() {
    fun markAsRead(id: String) {
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
            sharedRepo.markNotificationRead(id)
        }
    }
}