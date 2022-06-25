package com.shiftboard.schedulepro.core.network.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.messaging.MessagingTemplates
import com.shiftboard.schedulepro.core.messaging.NotificationConstants
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import com.shiftboard.schedulepro.core.persistence.model.PageKey
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import org.threeten.bp.OffsetDateTime
import java.io.IOException
import java.io.InvalidObjectException

class NotificationSource(
    private val api: ScheduleProApi,
    private val db: AppDatabase
) : RemoteMediator<Int, Notification>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Notification>,
    ): MediatorResult {
        val key = db.pageDao().findKey(PAGE_NAME)

        val page: Int = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                if (key == null) {
                    throw InvalidObjectException("No page key found for notification list")
                }

                key.nextKey?.toIntOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val results = api.fetchNotificationPage(page)
            if (!results.isSuccessful) {
                return MediatorResult.Error(NetworkError(results.message()))
            }
            val cachedAt = OffsetDateTime.now()
            val data = results.body() ?: return MediatorResult.Error(EmptyBodyException())
            val notifications = data.data.map { it.map(cachedAt) }
                    // Only pass notification types that we can possibly map
                .filter { it.type in NotificationConstants.allTypes }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.notificationDao().deleteAllNotifications()
                    db.pageDao().removeKey(PAGE_NAME)
                }

                db.notificationDao().insertNotifications(notifications)
                db.pageDao().insert(PageKey(PAGE_NAME, (page + 1).toString(), null))
            }

            return MediatorResult.Success(notifications.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    companion object {
        const val PAGE_NAME = "notification_list"
    }
}