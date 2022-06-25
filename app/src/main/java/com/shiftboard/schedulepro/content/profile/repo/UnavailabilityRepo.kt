package com.shiftboard.schedulepro.content.profile.repo

import android.util.Log
import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.profile.Unavailability
import com.shiftboard.schedulepro.core.network.model.profile.Recurrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnavailabilityRepo(
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {
    suspend fun getUnavailabilities(employeeId: String): Maybe<List<Unavailability>> {
        try {
            return withContext(Dispatchers.IO) {
                val result = api.getAvailabilities(employeeId)
                if (!result.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(result.message()))
                }
                val body = result.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body.unavailabilities)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun getRecurrences(employeeId: String): Maybe<List<Recurrence>> {
        try {
            return withContext(Dispatchers.IO) {
                val result = api.getRecurrences(employeeId)
                if (!result.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(result.message()))
                }
                val body = result.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body.unavailabilityRecurrences)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun addUnavailabilities(unavailability: Unavailability): Maybe<String> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.addUnavailabilities(unavailability)
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

    suspend fun addRecurrences(recurrence: Recurrence): Maybe<String> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.addRecurrences(recurrence)
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

    suspend fun deleteUnavailability(unavailabilityId: String): Maybe<Boolean> {
        try {
            return withContext(Dispatchers.IO) {
                val result = api.deleteUnavailability(unavailabilityId = unavailabilityId)
                if (!result.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(result.message()))
                }
                val body = result.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(true)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }


    suspend fun deleteRecurrence(unavailabilityRecurrenceId: String): Maybe<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val results = api.deleteRecurrence(unavailabilityRecurrenceId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }

                val data =
                    results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(true)
            } catch (e: Exception) {
                return@withContext Maybe.Failure(e)
            }
        }

    }
}