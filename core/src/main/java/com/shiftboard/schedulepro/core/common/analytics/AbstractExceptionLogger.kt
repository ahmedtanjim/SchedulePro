package com.shiftboard.schedulepro.core.common.analytics

import org.koin.core.KoinComponent

abstract class AbstractExceptionLogger: AnalyticsProperties {
    abstract fun enabled(isEnabled: Boolean)

    abstract fun recordException(throwable: Throwable)
    abstract fun log(message: String)
}

object ExceptionHandler: KoinComponent {
    fun recordException(throwable: Throwable) {
        getKoin().getOrNull<AbstractExceptionLogger>()?.recordException(throwable)
    }

    fun log(message: String) {
        getKoin().getOrNull<AbstractExceptionLogger>()?.log(message)
    }
}