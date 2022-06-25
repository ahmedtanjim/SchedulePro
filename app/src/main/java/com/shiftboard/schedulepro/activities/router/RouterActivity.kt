package com.shiftboard.schedulepro.activities.router

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shiftboard.schedulepro.activities.auth.AuthActivity
import com.shiftboard.schedulepro.activities.root.RootActivity
import com.shiftboard.schedulepro.activities.splash.SplashActivity
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.core.messaging.NotificationConstants
import org.koin.androidx.viewmodel.ext.android.viewModel


class RouterActivity : AppCompatActivity() {
    private val viewModel by viewModel<RouterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This shouldn't happen but reasons
        if (intent == null) {
            startIntent(SplashActivity::class.java, null)
            return
        }

        if (intent.hasExtra(NotificationConstants.EXTRA_ID)) {
            viewModel.markAsRead(intent.getStringExtra(NotificationConstants.EXTRA_ID) ?: "")
        }

        // We need to login
        val currState = CredentialPrefs.getCurrent()
        if (!currState.isAuthorized) {
            startIntent(AuthActivity::class.java, intent)
            return
        }

        val actService = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = actService.appTasks

        // We can open the root activity
        tasks.forEach {
            if (it.taskInfo.baseActivity == ComponentName.createRelative(this, "com.shiftboard.schedulepro.activities.root.RootActivity")) {
                startIntent(RootActivity::class.java, intent)
                return
            }
        }

        // Default to the splash activity
        startIntent(SplashActivity::class.java, intent)
    }

    private fun startIntent(clazz: Class<*>, startIntent: Intent?) {
        startActivity(Intent(this, clazz).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            if (startIntent?.extras != null) {
                putExtras(startIntent)
            }
        })
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}