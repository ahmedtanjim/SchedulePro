package com.shiftboard.schedulepro.content.notifications.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.paging.NotificationSource
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepo(
    private val db: AppDatabase,
    private val api: ScheduleProApi,

    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {
    @OptIn(ExperimentalPagingApi::class)
    fun getNotificationList(): Flow<PagingData<Notification>> {
        return Pager(
            // TODO :: this number is still arbitrary so is hardcoded till design is worked out and
            //  I can get a better sense of what it should be
            config = PagingConfig(50),
            remoteMediator = NotificationSource(api, db)
        ) {
            db.notificationDao().fetchAllNotifications()
        }.flow
    }
}