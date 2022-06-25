package com.shiftboard.schedulepro.core.common.base

import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.analytics.AbstractExceptionLogger
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.common.TokenRefresher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber


abstract class BaseRepository(
    networkCallImpl: BaseNetworkCall
): BaseNetworkCall by networkCallImpl

interface BaseNetworkCall {
    suspend fun <T : Any> networkCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        call: suspend () -> Response<T>,
    ): Maybe<T>
}

class TokenRefreshBaseNetworkCall(
    private val exceptionHandler: AbstractExceptionLogger,
    private val refresher: TokenRefresher
): BaseNetworkCall {

    override suspend fun <T : Any> networkCall(
        dispatcher: CoroutineDispatcher,
        call: suspend () -> Response<T>,
    ): Maybe<T> {
        return withContext(dispatcher) {
            try {
                // Refresh tokens if needed.  We don't care about the results of this
                // we only need to call it
                refresher.refreshToken()

                val response = call.invoke()
                if (response.isSuccessful) {
                    response.body()?.let { Maybe.Success(it) }
                        ?: Maybe.Failure(EmptyBodyException())
                } else {
                    Maybe.Failure(NetworkError(response.message()))
                }
            } catch (e: Exception) {
                exceptionHandler.recordException(e)
                Maybe.Failure(e)
            }
        }
    }
}

