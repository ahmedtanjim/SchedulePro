package com.shiftboard.schedulepro.core.persistence

sealed class CacheResponse<T> {
    data class Loading<T>(val data: T? = null): CacheResponse<T>()
    data class Failure<T>(val exception: Throwable?, val data: T? = null): CacheResponse<T>()

    data class Success<T>(val data: T): CacheResponse<T>()
}