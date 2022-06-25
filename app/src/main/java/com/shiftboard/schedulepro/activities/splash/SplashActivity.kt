package com.shiftboard.schedulepro.activities.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.auth.AuthActivity
import com.shiftboard.schedulepro.activities.auth.AuthManager
import com.shiftboard.schedulepro.activities.root.RootActivity
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.core.common.MobileNotEnabled
import com.shiftboard.schedulepro.core.common.base.BaseScopedActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class SplashActivity : BaseScopedActivity() {

    private val splashViewModel by viewModel<SplashActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currState = CredentialPrefs.getCurrent()

        splashViewModel.prepAppState.observeNotNull {
            it.doUnlessHandledOrNull { state ->
                when (state) {
                    is SplashPrepState.Error -> {
                        when (state.throwable) {
                            is MobileNotEnabled -> handleMobileNotEnabled()
                            else -> handleError(state.throwable)
                        }

                    }
                    is SplashPrepState.Ready -> {
                        startActivity(
                            Intent(this@SplashActivity, RootActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                if (intent.extras != null) {
                                    putExtras(intent)
                                }
                            }
                        )
                        finish()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                    else -> {
                    }
                }
            }
        }

        if (currState.isAuthorized) {
            splashViewModel.loadApp()
        } else {
            startActivity(Intent(this, AuthActivity::class.java).apply {
                if (intent.extras != null) {
                    putExtras(intent)
                }
            })
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }


    private fun handleMobileNotEnabled() {
        MaterialDialog(this@SplashActivity).show {
            lifecycleOwner(this@SplashActivity)
            title(R.string.mobile_access_disabled_title)
            cancelOnTouchOutside(false)
            cancelable(false)
            message(R.string.mobile_access_disabled)
            positiveButton(R.string.ok) {
                lifecycleScope.launchWhenCreated {
                    AuthManager().logout(this@SplashActivity)
                }
            }
        }
    }

    private fun handleError(error: Throwable?) {
        MaterialDialog(this@SplashActivity).show {
            lifecycleOwner(this@SplashActivity)
            title(R.string.error)
            cancelOnTouchOutside(false)
            cancelable(false)
            message(text = error?.message ?: "Unknown Error")
            positiveButton(R.string.ok) {
                lifecycleScope.launchWhenCreated {
                    AuthManager().logout(this@SplashActivity)
                }
            }
        }
    }
}