package com.shiftboard.schedulepro

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shiftboard.schedulepro.common.utils.AbstractFlavorSettings
import com.shiftboard.schedulepro.core.Environment
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

object FlavorSettings: AbstractFlavorSettings {
    override fun defaultEnvironment(): Environment = Environment.QA

    override fun httpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    override fun versionName(): String = "${BuildConfig.VERSION_NAME}-QA"
    override fun canUseDebugMode(): Boolean = true

    override fun initFlavor(app: Application) {
        Timber.plant(Timber.DebugTree())

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(true)
    }
}