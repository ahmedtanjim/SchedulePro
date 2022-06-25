package com.shiftboard.schedulepro.activities.auth

import android.app.Activity
import android.content.Intent
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.activities.splash.SplashActivity
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.AbstractExceptionLogger
import com.shiftboard.schedulepro.core.common.analytics.UserActionAnalyticEvents
import com.shiftboard.schedulepro.core.persistence.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import okhttp3.Cache
import org.koin.core.KoinComponent
import org.koin.core.get

class AuthManager: KoinComponent {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun logout(activity: Activity) {
        val analyticsProvider = get<AbstractAnalyticsProvider>()
        val exceptionLogger = get<AbstractExceptionLogger>()

        analyticsProvider.logEvent(UserActionAnalyticEvents.Logout)

        analyticsProvider.setUserId("")
        analyticsProvider.setCustomProperty("group_id", "")
        analyticsProvider.setCustomProperty("organization_id", "")
        analyticsProvider.setCustomProperty("team_id", "")

        exceptionLogger.setUserId("")
        exceptionLogger.setCustomProperty("group_id", "")
        exceptionLogger.setCustomProperty("organization_id", "")
        exceptionLogger.setCustomProperty("team_id", "")

        withContext(Dispatchers.IO) {
            // Clear database
            val db = get<AppDatabase>()

            db.nukeDao().nukeDb()

            // Clear the user prefs
            CredentialPrefs.keyState = AuthState()
            CredentialPrefs.userId = ""

            // Reset FCM sync
            AppPrefs.tokenSynced = 0

            // Clear the cache
            val cache = get<Cache>()
            // We get the BlockingMethodInNonBlockingContext warning for this, but we are in a coroutine
            // on an IO context so we should be fine
            cache.evictAll()
        }

        analyticsProvider.logEvent(UserActionAnalyticEvents.Logout)

        withContext(Dispatchers.Main) {
            activity.startActivity(Intent(activity, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            })
            activity.finishAffinity()
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}