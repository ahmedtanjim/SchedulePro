package com.shiftboard.schedulepro.common.inject

import android.annotation.SuppressLint
import com.google.firebase.analytics.FirebaseAnalytics
import com.shiftboard.schedulepro.FlavorSettings

import com.shiftboard.schedulepro.common.inject.providers.FirebaseAnalyticsProvider
import com.shiftboard.schedulepro.common.inject.providers.FirebaseExceptionLogger
import com.shiftboard.schedulepro.common.inject.providers.DatabaseCredentialsProviderImpl
import com.shiftboard.schedulepro.common.inject.providers.FcmTokenInterfaceImpl
import com.shiftboard.schedulepro.common.inject.providers.FcmUserValidatorImpl
import com.shiftboard.schedulepro.common.inject.providers.NetworkCredentialProviderImpl
import com.shiftboard.schedulepro.core.EnvironmentConfig
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.AbstractExceptionLogger
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.messaging.FcmTokenInterface
import com.shiftboard.schedulepro.core.network.common.NetworkCredentialProvider
import com.shiftboard.schedulepro.core.messaging.FcmUserValidator
import com.shiftboard.schedulepro.core.persistence.DatabaseCredentialsProvider
import com.shiftboard.schedulepro.core.persistence.MonthLruCache
import com.shiftboard.schedulepro.core.persistence.preference.PermissionRepo
import com.shiftboard.schedulepro.core.persistence.preference.UserPreferences

import org.koin.dsl.module

@SuppressLint("MissingPermission")
object AppModule {

    val module = module {

        single<AbstractAnalyticsProvider> { FirebaseAnalyticsProvider(FirebaseAnalytics.getInstance(get())) }
        single<AbstractExceptionLogger> { FirebaseExceptionLogger() }

        factory<DatabaseCredentialsProvider> { DatabaseCredentialsProviderImpl() }
        factory<NetworkCredentialProvider> { NetworkCredentialProviderImpl() }
        factory<FcmTokenInterface> { FcmTokenInterfaceImpl() }
        factory<FcmUserValidator> { FcmUserValidatorImpl(get()) }
        factory<EnvironmentConfig> { AppPrefs.environment }

        factory { MonthLruCache(5) }

        single { FlavorSettings.httpLoggingInterceptor() }

        single { UserPreferences(get()) }
        // Not technically a repo its more of a dao
        single { PermissionRepo(get()) }
    }
}