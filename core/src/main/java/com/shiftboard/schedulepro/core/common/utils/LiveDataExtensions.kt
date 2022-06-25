@file:Suppress("unused")
package com.shiftboard.schedulepro.core.common.utils

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*

@MainThread
fun <T> MutableLiveData<T>.touch() {
    value = value
}

@MainThread
fun <T> MutableLiveData<T>.touchNotNull() {
    if (value != null) value = value
}

fun <T> mutableLiveData(value: T?): MutableLiveData<T> = MutableLiveData<T>().apply {
    postValue(value)
}

@AnyThread
fun <T> MutableLiveData<T>.postTouch() {
    postValue(value)
}

@AnyThread
fun <T> MutableLiveData<T>.mutate(block: (T?) -> T?) {
    postIfChanged(block(value))
}

@AnyThread
fun <T> MutableLiveData<T>.postTouchNotNull() {
    if (value != null) postValue(value)
}

class SingleValueLiveData<T>(val data: T): LiveData<T>() {
    override fun onActive() {
        super.onActive()
        postValue(data)
    }
}

@MainThread
fun <T> MutableLiveData<T>.updateIfChanged(newValue: T) {
    if (value != newValue) value = newValue
}

class NonNullMediatorLiveData<T> : MediatorLiveData<T>()

fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator: NonNullMediatorLiveData<T> = NonNullMediatorLiveData()
    mediator.addSource(this) { it?.let { mediator.value = it } }
    return mediator
}
fun <T> NonNullMediatorLiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}

class AbsentLiveData<T: Any?> private constructor(): LiveData<T>() {
    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }

        inline fun <R, T> switchMap(trigger: LiveData<T>, crossinline func: (T) -> LiveData<R>): LiveData<R> {
            return Transformations.switchMap(trigger) {
                when (it) {
                    null -> create()
                    else -> func(it)
                }
            }
        }

        inline fun <R, T> switchMap(trigger: LiveData<T>, absentValue: T, crossinline func: (T) -> LiveData<R>): LiveData<R> {
            return Transformations.switchMap(trigger) {
                when (it) {
                    null, absentValue -> create()
                    else -> func(it)
                }
            }
        }

        inline fun <R, T> switchMap(trigger: LiveData<T>, vararg absentValue: T, crossinline func: (T) -> LiveData<R>): LiveData<R> {
            return Transformations.switchMap(trigger) {
                when (it) {
                    null, in absentValue -> create()
                    else -> func(it)
                }
            }
        }
    }
}

fun <T> MutableStateFlow<T>.eventWrapper(): Flow<Event<T>> = map { Event(it) }

fun <T> LiveData<T>.eventWrapper() : LiveData<Event<T>> = Transformations.map(this) { Event(it) }

class SimpleLiveDataEvent {
    var hasBeenHandled = false
        private set

    fun runIfNotHandled(block: ()->Unit) {
        if (!hasBeenHandled) {
            hasBeenHandled = true
            block()
        }
    }

    companion object {
        fun liveData() = SimpleEventLiveData()
    }
}

class SimpleEventLiveData: MutableLiveData<SimpleLiveDataEvent>() {
    fun call() {
        value = SimpleLiveDataEvent()
    }

    fun immutable(): LiveData<SimpleLiveDataEvent> = this
}

class ReloadTransformMediator<T, R>(source: LiveData<T>, private val transformer: (T)->R): MediatorLiveData<R>() {
    init {
        addSource(source) {
            value = transformer(it)
        }
    }
}

fun <T> MutableLiveData<T>.touchIfChanged(newValue: T?) {
    if (value != newValue) {
        value = newValue
    }
}

fun <T> MutableLiveData<T>.postIfChanged(newValue: T?) {
    if (value != newValue) {
        postValue(newValue)
    }
}

fun <T> LiveData<T>.asNonNullFlow(): Flow<T> = asFlow().filter { it != null }.distinctUntilChanged()


fun <V, T, R> LiveData<V>.combine(other: LiveData<T>, merger: suspend (V, T) -> R): LiveData<R> {
    return asFlow().filter { it != null }.combine(other.asFlow().filter { it != null }, merger).asLiveData()
}

fun <V, T> LiveData<V>.combineToPair(other: LiveData<T>): LiveData<Pair<V, T>> {
    return asFlow().filter { it != null }.combine(other.asFlow().filter { it != null }) { a, b -> a to b }.asLiveData()
}