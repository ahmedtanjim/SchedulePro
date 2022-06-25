package com.shiftboard.schedulepro.activities.auth.repo

import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepo(
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    suspend fun postLogInEvent(): Maybe<Boolean> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.postLogInEvent()
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