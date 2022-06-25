package com.shiftboard.schedulepro.core.network.common

import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.persistence.Cache
import com.shiftboard.schedulepro.core.persistence.CacheResponse
import com.shiftboard.schedulepro.core.persistence.Cached
import com.shiftboard.schedulepro.core.persistence.Mappable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response


fun <R: Mappable<T>, T> flowCache(
    fetchDatabase: suspend ()->T?,
    fetchApi: suspend ()->Response<R>,
    cacheData: suspend (T)->Unit,
): Flow<CacheResponse<T>> where T : Any, T: Cached {
    return flow<CacheResponse<T>> {
        emit(CacheResponse.Loading())

        val dbCache = fetchDatabase()
        dbCache?.takeIf { it.cachedAt.isAfter(Cache.shortTimestamp) }?.let {
            emit(CacheResponse.Loading(it))
        }

        val results = flowCacheNetworkCall(fetchApi)
        val data = results.map()
        cacheData(data)

        emit(CacheResponse.Success(data))

    }.catch { emit(CacheResponse.Failure(it)) }
}

fun <R: Mappable<T>, T, U> flowTransformCache(
    fetchDatabase: suspend ()->T?,
    fetchApi: suspend ()->Response<R>,
    transform: suspend (T) -> U,
    cacheData: suspend (T)->Unit,
): Flow<CacheResponse<U>> where T : Any, T: Cached, U: Any {
    return flow<CacheResponse<U>> {
        emit(CacheResponse.Loading())

        val dbCache = fetchDatabase()
        dbCache?.takeIf { it.cachedAt.isAfter(Cache.shortTimestamp) }?.let {
            emit(CacheResponse.Loading(transform(it)))
        }

        val results = flowCacheNetworkCall(fetchApi)
        val data = results.map()
        cacheData(data)

        emit(CacheResponse.Success(transform(data)))

    }.catch { emit(CacheResponse.Failure(it)) }
}

suspend fun <R: Mappable<*>> flowCacheNetworkCall(fetchApi: suspend ()-> Response<R>): R {
    val results = fetchApi.invoke()
    if (!results.isSuccessful) {
        throw NetworkError(results.message())
    }
    return results.body() ?: throw EmptyBodyException()
}