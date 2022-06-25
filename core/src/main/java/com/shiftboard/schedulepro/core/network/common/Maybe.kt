package com.shiftboard.schedulepro.core.network.common

sealed class Maybe<T> {
    data class Success<T>(val data: T): Maybe<T>()
    data class Failure<T>(val exception: Throwable?): Maybe<T>()
}