package com.shiftboard.schedulepro.content.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.content.profile.repo.ProfileRepo

class ProfileViewModel(val profileRepo: ProfileRepo): ViewModel() {
    val userDetailsLiveData by lazy {
        profileRepo.userDetails()
            .asLiveData(viewModelScope.coroutineContext)
    }
}