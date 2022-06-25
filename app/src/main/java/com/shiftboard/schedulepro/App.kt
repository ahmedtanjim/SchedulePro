@file:Suppress("unused")
package com.shiftboard.schedulepro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.jakewharton.threetenabp.AndroidThreeTen
import com.shiftboard.schedulepro.common.inject.AppModule
import com.shiftboard.schedulepro.common.inject.Repositories
import com.shiftboard.schedulepro.common.inject.ViewModules
import com.shiftboard.schedulepro.core.common.CoreInject
import com.shiftboard.schedulepro.core.common.utils.DatastoreManager
import com.shiftboard.schedulepro.core.common.utils.PrefsManager
import com.shiftboard.schedulepro.core.messaging.NotificationConstants
import com.shiftboard.schedulepro.core.network.common.NetworkModule
import com.shiftboard.schedulepro.core.persistence.ScheduleProDB
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)
        PrefsManager.init(this)
        DatastoreManager.init(this)

        FlavorSettings.initFlavor(this)

        createNotificationChannel()

        startKoin {
            if (FlavorSettings.canUseDebugMode()) {
                androidLogger(Level.DEBUG)
            }
            androidContext(this@App)
            modules(listOf(
                AppModule.module,
                CoreInject.module,
                Repositories.module,
                ViewModules.module,

                NetworkModule.module,
                ScheduleProDB.module,
            ))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(NotificationChannel(
                NotificationConstants.LEAVE_UPDATES_CHANNEL,
                getString(R.string.leave_updates_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.leave_updates_channel_description)
            })

            notificationManager.createNotificationChannel(NotificationChannel(
                NotificationConstants.BULK_MESSAGE_CHANNEL,
                getString(R.string.new_message),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.new_message_description)
            })

            notificationManager.createNotificationChannel(NotificationChannel(
                NotificationConstants.SHIFT_UPDATES_CHANNEL,
                getString(R.string.shift_updates_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.shift_updates_channel_description)
            })

            notificationManager.createNotificationChannel(NotificationChannel(
                NotificationConstants.SCHEDULE_UPDATES_CHANNEL,
                getString(R.string.schedule_updates_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.schedule_updates_channel_description)
            })
        }
    }
}