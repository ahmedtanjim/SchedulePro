package com.shiftboard.schedulepro.content.group.domain

import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.group.GroupFiltersResponse
import com.shiftboard.schedulepro.core.network.model.group.GroupSchedulesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class GroupsRepo(
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {


    suspend fun getFilters(): Maybe<GroupFiltersResponse> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.getGroupFilters()
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun getGroupSchedule(startDate: LocalDate, endDate: LocalDate, filters: GroupFiltersResponse): Maybe<GroupSchedulesResponse> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.getGroupSchedule(startDate.serverFormat(), endDate.serverFormat(), filters)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }
}
