package com.shiftboard.schedulepro.content.leaves.repo

import com.shiftboard.schedulepro.content.leaves.request.PostLeaveRequest
import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.LeaveElementModel
import com.shiftboard.schedulepro.core.network.model.LeaveRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.format.DateTimeFormatter

class LeaveRepo(
    private val api: ScheduleProApi,

    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    suspend fun fetchLeaveList(): Maybe<List<LeaveElementModel>> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.listLeaveTypes()
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

    suspend fun submitLeaveRequest(event: PostLeaveRequest): Maybe<Boolean> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.postLeaveRequest(LeaveRequest(
                    leaveTypeId = event.type,
                    startDate = event.startDate.atStartOfDay()
                        .format(DateTimeFormatter.ISO_DATE_TIME),
                    endDate = event.endDate.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME),
                    comment = event.notes.takeIf { it.isNotBlank() }
                ))
                if (results.isSuccessful) {
                    return@withContext Maybe.Success(true)
                }
                return@withContext Maybe.Failure(NetworkError(results.message()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Maybe.Failure(e)
        }
    }
}