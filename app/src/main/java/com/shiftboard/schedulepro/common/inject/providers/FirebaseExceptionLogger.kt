package com.shiftboard.schedulepro.common.inject.providers

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shiftboard.schedulepro.core.common.analytics.AbstractExceptionLogger
import timber.log.Timber

class FirebaseExceptionLogger : AbstractExceptionLogger() {
    override fun enabled(isEnabled: Boolean) {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(isEnabled)
    }

    override fun setUserId(id: String) {
        FirebaseCrashlytics.getInstance().setUserId(id)
    }

    override fun setCustomProperty(key: String, value: String) {
        FirebaseCrashlytics.getInstance().setCustomKey(key, value)
    }

    override fun recordException(throwable: Throwable) {
        // when we are in debug mode we want to also log to the console
        Timber.d(throwable)
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    override fun log(message: String) {
        Timber.d(message)
        FirebaseCrashlytics.getInstance().log(message)
    }
}