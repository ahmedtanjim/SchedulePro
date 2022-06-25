@file:Suppress("unused")
package com.shiftboard.schedulepro.core.common.utils

/**
 * Extensions for the kotlinx.coroutines library
 */

import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@MainThread
inline fun <T> Flow<T>.observe(scope: CoroutineScope, crossinline onChanged: (T) -> Unit): Job {
    return onEach {
        onChanged(it)
    }.launchIn(scope)
}

@MainThread
inline fun <T> Flow<T>.observeLatest(scope: CoroutineScope, crossinline onChanged: suspend (T) -> Unit) {
    scope.launch {
        collectLatest {
            onChanged(it)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <E> SendChannel<E>.safeOffer(value: E): Boolean {
    return runCatching { trySend(value).isSuccess }.getOrDefault(false)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.startWithCurrentValue(emitImmediately: Boolean, block: () -> T?): Flow<T> {
    return if (emitImmediately) onStart {
        block()?.run { emit(this) }
    } else this
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <E> BroadcastChannel<E>.debouncedFlow(timeout: Long): Flow<E> = asFlow().debounce(timeout)
