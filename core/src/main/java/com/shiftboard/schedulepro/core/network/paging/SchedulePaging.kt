package com.shiftboard.schedulepro.core.network.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.shiftboard.schedulepro.core.common.utils.atEndOfMonth
import com.shiftboard.schedulepro.core.common.utils.atStartOfMonth
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.model.schedule.ScheduleElementModel
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import com.shiftboard.schedulepro.core.persistence.model.PageKey
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Response
import timber.log.Timber
import java.io.InvalidObjectException


class ScheduleSourceMediator(
    private val api: ScheduleProApi,
    private val db: AppDatabase,
    private val baseKey: LocalDate,
) : RemoteMediator<Int, DayScheduleElement>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DayScheduleElement>,
    ): MediatorResult {
        try {
            val key = db.pageDao().findKey(PAGE_NAME)

            val pageStart: LocalDate
            val pageEnd: LocalDate

            when (loadType) {
                LoadType.REFRESH -> {
                    pageStart = baseKey.atStartOfMonth()
                    pageEnd = pageStart.atEndOfMonth()
                }
                LoadType.PREPEND -> {
                    if (key == null) {
                        throw InvalidObjectException("No page key found for notification list")
                    }

                    pageStart = key.prevKey?.let { LocalDate.parse(it) }
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    pageEnd = pageStart.atEndOfMonth()
                }
                LoadType.APPEND -> {
                    if (key == null) {
                        throw InvalidObjectException("No page key found for notification list")
                    }

                    pageStart = key.nextKey?.let { LocalDate.parse(it) }
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    pageEnd = pageStart.atEndOfMonth()
                }
            }

            val response = fetchSchedule(
                pageStart.format(DateTimeFormatter.ISO_LOCAL_DATE),
                pageEnd.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
            val scheduleList = response.body() ?: listOf()

            val cachedAt = OffsetDateTime.now()
            val mappedList = scheduleList.map { it.map(cachedAt) }

            if (mappedList.isEmpty()) {
                withContext(Dispatchers.IO) {
                    db.withTransaction {
                        val pageKey = when (loadType) {
                            LoadType.REFRESH -> PageKey(PAGE_NAME, null, null)
                            LoadType.PREPEND-> PageKey(PAGE_NAME, pageStart.plusMonths(1L).format(DateTimeFormatter.ISO_LOCAL_DATE), null)
                            LoadType.APPEND -> PageKey(PAGE_NAME, null, pageStart.plusMonths(-1L).format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }

                        db.pageDao().insert(pageKey)
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            withContext(Dispatchers.IO) {
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        db.scheduleDao().deleteAllData()
                        db.pageDao().removeKey(PAGE_NAME)
                    }
                    mappedList.forEach {
                        db.scheduleDao().insertScheduleElement(it.base)

                        db.scheduleDao().insertAllShifts(it.shiftElements)
                        db.scheduleDao().insertAllLeaves(it.leaveElements)
                        db.scheduleDao().insertAllPendingLeaves(it.pendingLeaveElements)
                    }

                    val pageKey = when (loadType) {
                        LoadType.REFRESH -> PageKey(PAGE_NAME, pageStart.plusMonths(1L).format(DateTimeFormatter.ISO_LOCAL_DATE), pageStart.plusMonths(-1L).format(DateTimeFormatter.ISO_LOCAL_DATE))
                        LoadType.PREPEND-> PageKey(PAGE_NAME, key?.nextKey, pageStart.plusMonths(-1L).format(DateTimeFormatter.ISO_LOCAL_DATE))
                        LoadType.APPEND -> PageKey(PAGE_NAME, pageStart.plusMonths(1L).format(DateTimeFormatter.ISO_LOCAL_DATE), key?.prevKey)
                    }
                    db.pageDao().insert(pageKey)
                }
            }

            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.e(e)
            return MediatorResult.Error(e)
        }
    }

    private suspend fun fetchSchedule(startDate: String, endDate: String): Response<List<ScheduleElementModel>> {
        return api.fetchSchedule(startDate, endDate)
    }

    companion object {
        const val PAGE_NAME = "schedule_list"
    }
}