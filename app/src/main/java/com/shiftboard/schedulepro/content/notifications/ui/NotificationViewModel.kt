package com.shiftboard.schedulepro.content.notifications.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shiftboard.schedulepro.content.notifications.repo.NotificationRepo
import kotlinx.coroutines.flow.MutableStateFlow

class NotificationViewModel(notificationRepo: NotificationRepo): ViewModel() {

    var scrollState = 0

    val notificationData= notificationRepo.getNotificationList()
        .cachedIn(viewModelScope)
}