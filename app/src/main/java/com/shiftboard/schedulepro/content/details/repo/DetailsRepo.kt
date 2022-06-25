package com.shiftboard.schedulepro.content.details.repo

import androidx.room.withTransaction
import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.common.flowCache
import com.shiftboard.schedulepro.core.network.common.flowTransformCache
import com.shiftboard.schedulepro.core.network.model.details.LeaveRequestAction
import com.shiftboard.schedulepro.core.network.model.openshift.OpenShiftDetails
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import com.shiftboard.schedulepro.core.persistence.model.PermissionPrefs
import com.shiftboard.schedulepro.core.persistence.model.details.LeaveRequestDetails
import com.shiftboard.schedulepro.core.persistence.model.details.ShiftDetails
import com.shiftboard.schedulepro.core.persistence.preference.PermissionRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class DetailsRepo(
    private val db: AppDatabase,
    private val permissionRepo: PermissionRepo,
    private val api: ScheduleProApi,

    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    fun getLeaveRequestById(requestId: String) = flowCache(
        { db.detailsDao().findLeaveRequestDetailsById(requestId) },
        { api.fetchLeaveRequestDetails(requestId) },
        { db.withTransaction { db.detailsDao().insert(it) } }
    )

    fun getLeaveById(leaveId: String) = flowCache(
        { db.detailsDao().findLeaveDetailsById(leaveId) },
        { api.fetchLeaveDetails(leaveId) },
        {
            db.withTransaction {
                it.leaveRequest?.let { request -> db.detailsDao().insert(request) }
                db.detailsDao().insert(it.leave)
            }
        }
    )

    fun getProjectedLeave(date: LocalDate, leaveId: String) = flowCache(
        { db.detailsDao().findProjectedLeaveRequestDetails(date, leaveId) },
        { api.fetchProjectedLeaveDetails(date.serverFormat(), leaveId) },
        { db.withTransaction { db.detailsDao().insert(it) } }
    )

    fun getProjectedShift(date: LocalDate, shiftId: String) = flowCache(
        { db.detailsDao().findProjectedShiftRequestDetails(date, shiftId) },
        { api.fetchProjectedShiftDetails(date.serverFormat(), shiftId) },
        { db.withTransaction { db.detailsDao().insert(it) } }
    )

    fun getShiftById(shiftId: String) = flowTransformCache(
            { db.detailsDao().findShiftDetailsById(shiftId) },
            { api.fetchShiftDetails(shiftId) },
            {
                val permissions = permissionRepo.getCurrent()
                PermissionShiftDetails(permissions, it)
            },
            { db.withTransaction { db.detailsDao().insert(it) } }
        )

    suspend fun postTurndown(shiftId: String): Maybe<OpenShiftDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val results = api.postTurndownShift(shiftId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }

                val data =
                    results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(data)
            } catch (e: Exception) {
                return@withContext Maybe.Failure(e)
            }
        }
    }

    suspend fun cancelTurndown(shiftId: String): Maybe<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val results = api.deleteTurndownShift(shiftId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }

                val data =
                    results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(data)
            } catch (e: Exception) {
                return@withContext Maybe.Failure(e)
            }
        }
    }
    suspend fun postRequestDelete(requestId: String): Maybe<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val results = api.leaveRequestDelete(requestId, LeaveRequestAction.deleteRequest())
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }

                db.withTransaction {
                    db.detailsDao().deleteLeaveRequestDetailsById(requestId)
                }

                return@withContext Maybe.Success(true)
            } catch (e: Exception) {
                return@withContext Maybe.Failure(e)
            }
        }
    }

    suspend fun postRequestCancellation(requestId: String): Maybe<LeaveRequestDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val results =
                    api.leaveRequestCancel(requestId, LeaveRequestAction.cancellationRequest())
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val data =
                    results.body()?.map() ?: return@withContext Maybe.Failure(EmptyBodyException())

                db.withTransaction {
                    db.detailsDao().insert(data)
                }
                return@withContext Maybe.Success(data)
            } catch (e: Exception) {
                return@withContext Maybe.Failure(e)
            }

        }
    }
}

class PermissionShiftDetails(
    val permissions: PermissionPrefs,
    val shiftDetails: ShiftDetails
)