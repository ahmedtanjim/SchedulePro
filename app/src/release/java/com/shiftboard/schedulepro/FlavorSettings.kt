package com.shiftboard.schedulepro

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shiftboard.schedulepro.common.utils.AbstractFlavorSettings
import com.shiftboard.schedulepro.core.Environment
import okhttp3.logging.HttpLoggingInterceptor

object FlavorSettings: AbstractFlavorSettings {
    override fun defaultEnvironment(): Environment = Environment.PRODUCTION
    override fun httpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .apply { level = HttpLoggingInterceptor.Level.NONE }

    override fun versionName(): String = BuildConfig.VERSION_NAME
    override fun canUseDebugMode(): Boolean = false

    override fun initFlavor(app: Application) {
        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(true)
    }
}