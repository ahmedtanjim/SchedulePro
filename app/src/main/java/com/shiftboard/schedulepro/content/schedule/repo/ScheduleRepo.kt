package com.shiftboard.schedulepro.content.schedule.repo

import androidx.room.withTransaction
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.schedule.SummaryDate
import com.shiftboard.schedulepro.core.network.model.schedule.SummaryIconModel
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import com.shiftboard.schedulepro.core.persistence.LruKey
import com.shiftboard.schedulepro.core.persistence.MonthLruCache
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import com.shiftboard.schedulepro.core.persistence.preference.PermissionRepo
import kotlinx.coroutines.flow.*
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.lang.Exception


class ScheduleRepo(
    private val api: ScheduleProApi,
    private val db: AppDatabase,
    private val lruCache: MonthLruCache,
    private val permissionRepo: PermissionRepo,

    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    suspend fun deleteAllScheduleData() {
        db.withTransaction {
            db.scheduleDao().deleteAllData()
        }
    }

    suspend fun cacheNewScheduleData(start: LocalDate, end: LocalDate) {
        val days = ChronoUnit.DAYS.between(start, end)
        if (days >= 94) {
            return
        }

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
            db.scheduleDao().deleteProjectedShiftElementInRange(start, end)
            db.scheduleDao().deleteProjectedLeaveElementInRange(start, end)
            db.scheduleDao().deleteOpenShiftElementInRange(start, end)
            db.scheduleDao().deleteOpenShiftEventInRange(start, end)
            db.scheduleDao().deleteTradeEventInRange(start, end)

            data.forEach {
                db.scheduleDao().insertScheduleElement(it.base)
                db.scheduleDao().insertSignupElement(it.signupElements)
                db.scheduleDao().insertSignupEvent(it.signupEvents)
                db.scheduleDao().insertAllShifts(it.shiftElements)
                db.scheduleDao().insertAllLeaves(it.leaveElements)
                db.scheduleDao().insertAllPendingLeaves(it.pendingLeaveElements)
                db.scheduleDao().insertProjectedShiftElement(it.projectedShift)
                db.scheduleDao().insertProjectedLeaveElement(it.projectedLeave)
                db.scheduleDao().insertOpenShiftElement(it.openShiftElements)
                db.scheduleDao().insertOpenShiftEvent(it.openShiftEvents)
                db.scheduleDao().insertTradeEvent(it.tradeEvents)
            }
        }
    }

    fun allScheduleData(): Flow<List<ScheduleItemElement>> {
        return permissionRepo.observe().flatMapLatest {
            db.scheduleDao().findAllShifts()
                .mapLatest {
                    it.flatMap { element ->
                        val elements = mutableListOf<ScheduleItemElement>()

                        // Add normal shift elements
                        element.shiftElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.SHIFT, item.startTime, item))
                        }
                        element.projectedShiftElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.PROJECTED_SHIFT, item.startTime, item))
                        }

                        element.leaveElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.LEAVE, item.startTime, item))
                        }
                        element.pendingLeaveElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.LEAVE_REQUEST, item.startTime, item))
                        }

                        element.projectedLeaveElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.PROJECTED_LEAVE, item.startTime, item))
                        }
                        element.openShiftElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.OPEN_SHIFT, null, item))
                        }

                        element.tradeEvents.forEach { item ->
                            elements.add(
                                ScheduleItemElement(
                                    false, element.base.date,
                                    ScheduleElementType.TRADE_REQUEST, null, item
                                )
                            )
                        }


                        // sort everything by start time
                        elements.sortedBy { item -> item.startTime ?: OffsetDateTime.MIN }

                        if (!element.base.holiday.isNullOrBlank()) {
                            // Prepend a holiday item a the start of the list
                            elements.add(0, ScheduleItemElement(false, element.base.date, ScheduleElementType.HOLIDAY,
                                null, HolidayItem(element.base.date, element.base.holiday ?: "")))
                        }

                        // add signups elements
                        element.signupElements.forEach { item ->
                            elements.add(ScheduleItemElement(false, element.base.date,
                                ScheduleElementType.SIGN_UPS,null, item))
                        }

                        // add actions container
                        elements.add(ScheduleItemElement(false, element.base.date,
                            ScheduleElementType.NONE,null, ActionsItem(
                                element.base.date, element.base.description, element.base.actions
                            )
                        ))


                        // tag the first element so we can add the date flag
                        elements.also { item -> item.firstOrNull()?.startOfDay = true }
                    }
                }.catch {
                    emit(listOf())
                }
        }
    }

    fun getScheduleSummary(start: LocalDate, end: LocalDate):
            Flow<CacheResponse<HashMap<LocalDate, List<SummaryIconModel>>>> {

        return flow<CacheResponse<HashMap<LocalDate, List<SummaryIconModel>>>> {
            emit(CacheResponse.Loading())

            // TODO :: we don't have filters yet so our filter key is 0 this is modeled after flex which uses filters
            val lruKey = LruKey(start,end, 0)

            lruCache[lruKey]?.let {
                emit(CacheResponse.Success(it))
            }

            val resultMap = HashMap<LocalDate, List<SummaryIconModel>>()
            val days = ChronoUnit.DAYS.between(start, end)
            if (days >= 94) {
                emit(CacheResponse.Failure(Exception("More Days Than 93 Exception Thrown")))
            }

            else {
                when (val results = getSummaryResults(start, end)) {
                    is Maybe.Success -> {
                        results.data.forEach {
                            resultMap[it.date.toLocalDate()] = it.items
                        }
                    }
                    is Maybe.Failure -> {
                        emit(CacheResponse.Failure(results.exception))
                        return@flow
                    }
                }
            }

            lruCache.put(lruKey, resultMap)
            emit(CacheResponse.Success(resultMap))
        }.catch {
            emit(CacheResponse.Failure(it))
        }
    }

    private suspend fun getSummaryResults(start: LocalDate, end: LocalDate): Maybe<List<SummaryDate>> {
        val results = networkCall {
            api.fetchScheduleSummary(start.serverFormat(), end.serverFormat())
        }

        return when (results) {
            is Maybe.Success -> Maybe.Success(results.data)
            is Maybe.Failure -> Maybe.Failure(results.exception)
        }
    }
}