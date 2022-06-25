package com.shiftboard.schedulepro.core.messaging

import androidx.room.withTransaction
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit


class MessagingRepo(
    private val db: AppDatabase,
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall) : BaseRepository(networkCallImpl) {

    suspend fun cacheNewScheduleData(start: LocalDate, end: LocalDate) {

            val results = networkCall {
                api.fetchSchedule(start.serverFormat(), end.serverFormat())
            }
            val cachedAt = OffsetDateTime.now()
            val data = when (results) {
                is Maybe.Success -> results.data.map { it.map(cachedAt) }
                is Maybe.Failure -> throw results.exception ?: NetworkError("Unknown Error")
            }
            db.withTransaction {
                db.scheduleDao().deleteScheduleElementsInRange(start, end)
                db.scheduleDao().deleteShiftElementsInRange(start, end)
                db.scheduleDao().deletePendingLeaveElementInRange(start, end)
                db.scheduleDao().deleteSignupElementsInRange(start, end)
                db.scheduleDao().deleteSignupEventsInRange(start, end)

                data.forEach {
                    db.scheduleDao().insertScheduleElement(it.base)

                    db.scheduleDao().insertAllShifts(it.shiftElements)
                    db.scheduleDao().insertAllLeaves(it.leaveElements)
                    db.scheduleDao().insertAllPendingLeaves(it.pendingLeaveElements)
                    db.scheduleDao().insertSignupElement(it.signupElements)
                    db.scheduleDao().insertSignupEvent(it.signupEvents)
                }
            }


    }

    suspend fun didRefreshSchedule(start: LocalDate, end: LocalDate) : Boolean {
        db.scheduleDao().findAllShifts().let { shift ->
            var s = shift.first().first().base.date
            if (s < start) s = start
            var e = shift.first().last().base.date
            if (e > end) e = end
            val days = ChronoUnit.DAYS.between(s, e);
            if (days >= 94) {
                return false
            }
            cacheNewScheduleData(s, e)
            return true
        }

    }

    suspend fun cacheNotificationData(data: Notification) {
        db.withTransaction {
            db.notificationDao().insertNotifications(listOf(data))
        }
    }

    suspend fun fetchNotificationDetails(notificationId: String): Maybe<Notification> {
        return when (val results = networkCall { api.fetchNotificationDetails(notificationId) }) {
            is Maybe.Success -> Maybe.Success(results.data.map())
            is Maybe.Failure -> Maybe.Failure(results.exception)
        }
    }
}