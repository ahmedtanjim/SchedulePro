package com.shiftboard.schedulepro.content.openShifts

import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.openshift.OpenShiftDetailsCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate

class OpenShiftRepo(
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    suspend fun fetchOpenShifts(date: LocalDate): Maybe<OpenShiftDetailsCollection> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.fetchOpenShifts(date.toString())
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Maybe.Failure(e)
        }
    }

    suspend fun postOpenShiftRequest(id: String): Maybe<Boolean> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.postOpenShiftRequestUpdate(id)
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

    suspend fun deleteOpenShiftRequest(id: String): Maybe<Boolean> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.deleteOpenShiftRequest(id)
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