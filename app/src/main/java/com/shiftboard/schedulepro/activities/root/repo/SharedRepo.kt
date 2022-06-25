package com.shiftboard.schedulepro.activities.root.repo

import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.notification.RegistrationRequest
import com.shiftboard.schedulepro.core.persistence.preference.UserPreferences


class SharedRepo(
    private val api: ScheduleProApi,
    private val userRepo: UserPreferences,
    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    suspend fun checkNotificationCount(): Maybe<Int> {
        return networkCall { api.fetchNotificationCount() }
    }

    suspend fun checkNotificationUser(userId: String): Boolean {
        val user = userRepo.getCurrent()
        return userId == user.id
    }

    suspend fun markNotificationRead(notificationId: String): Boolean {
        return when (networkCall { api.markNotificationRead(notificationId) }) {
            is Maybe.Success -> true
            is Maybe.Failure -> false
        }
    }

    suspend fun postFcmToken(token: String) = networkCall {
        api.postDeviceRegistration(RegistrationRequest(token))
    }
}