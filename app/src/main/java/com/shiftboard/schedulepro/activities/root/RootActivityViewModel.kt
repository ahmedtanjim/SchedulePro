package com.shiftboard.schedulepro.activities.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shiftboard.schedulepro.activities.root.repo.SharedRepo
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.network.common.Maybe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class RootActivityViewModel(private val sharedRepo: SharedRepo) : ViewModel() {
    fun syncFcmToken(token: String) {
        if (token != AppPrefs.fcmToken && token.isNotBlank()) {
            AppPrefs.fcmToken = token
            AppPrefs.tokenSynced = 0
        }
//        if (AppPrefs.tokenSynced < System.currentTimeMillis() - Duration.ofHours(6).toMillis()) {
            viewModelScope.launch(Dispatchers.IO) {
                when (sharedRepo.postFcmToken(AppPrefs.fcmToken)) {
                    is Maybe.Success -> {
                        AppPrefs.tokenSynced = System.currentTimeMillis()
                        Timber.d("Token Synced")
                    }
                    else -> {}
                }
            }
//        }
    }
}