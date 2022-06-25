package com.shiftboard.schedulepro.activities.root

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.shiftboard.schedulepro.HomeNavGraphDirections
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.core.common.base.BaseActivity
import com.shiftboard.schedulepro.core.common.base.BaseFragment
import com.shiftboard.schedulepro.core.common.utils.itemIds
import com.shiftboard.schedulepro.core.messaging.NotificationConstants
import com.shiftboard.schedulepro.databinding.RootActivityBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class RootActivity: BaseActivity() {
    private val viewModel by viewModel<RootActivityViewModel>()
    private val navController by lazy { findNavController(R.id.main_content) }
    private val sharedViewModel by viewModel<SharedStateViewModel>()
    private lateinit var boundView: RootActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boundView = RootActivityBinding.inflate(layoutInflater)
        setContentView(boundView.root)


        with(boundView) {
            bottomNav.setupWithNavController(navController)
            runBlocking {
                if (!sharedViewModel.groupPermission()){
                    bottomNav.menu.removeItem(R.id.group)
                }
            }

            sharedViewModel.snackNotifications.observe { it ->
                it?.doUnlessHandledOrNull {
                    lifecycleScope.launchWhenCreated {
                        delay(it.delay)
                        Snackbar.make(root, it.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            sharedViewModel.notificationCount.observeNotNull {
                val badge = bottomNav.getOrCreateBadge(R.id.notifications)
                if (it > 0) {
                    badge.isVisible = true
                    badge.number = it
                } else {
                    badge.isVisible = false
                }
            }
        }

        navController.addOnDestinationChangedListener { _, _, _ ->
            updateBottomNavState()
        }

        updateFcm()

        if (intent.hasExtra(NotificationConstants.EXTRA_START_DATE)) {
            val date = intent.getStringExtra(NotificationConstants.EXTRA_START_DATE) ?: return
            navController.navigate(HomeNavGraphDirections.actionGlobalSchedule().setStartDate(date))
        }

        if (intent?.hasExtra(NotificationConstants.EXTRA_BULK_MESSAGE) == true) {
            val message = intent.getStringExtra(NotificationConstants.EXTRA_BULK_MESSAGE) ?: return
            navController.navigate(HomeNavGraphDirections.actionGlobalNotification().setSelectedNotificationId(message))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.hasExtra(NotificationConstants.EXTRA_START_DATE) == true) {
            val date = intent.getStringExtra(NotificationConstants.EXTRA_START_DATE) ?: return
            navController.navigate(HomeNavGraphDirections.actionGlobalSchedule().setStartDate(date))
        }
    }

    override fun onResume() {
        super.onResume()
        updateBottomNavState()
    }

    private fun updateBottomNavState() {
        with(boundView) {
            bottomNav.isVisible = navController.currentDestination?.id in bottomNav.menu.itemIds()
        }
    }

    private fun updateFcm() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.d(task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result ?: ""
                viewModel.syncFcmToken(token)
            })
    }
}


abstract class BottomNavFragment: BaseFragment() {
    protected val sharedState by sharedViewModel<SharedStateViewModel>()

    override fun onResume() {
        super.onResume()
        sharedState.updateNotificationCount()
    }
}