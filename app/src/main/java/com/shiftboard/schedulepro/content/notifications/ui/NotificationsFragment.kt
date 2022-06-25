package com.shiftboard.schedulepro.content.notifications.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.HomeNavGraphDirections
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.root.BottomNavFragment
import com.shiftboard.schedulepro.content.notifications.trades.MessageDetailsFragment
import com.shiftboard.schedulepro.content.notifications.messages.TradeDetailsFragment
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.messaging.NotificationConstants
import com.shiftboard.schedulepro.core.messaging.focusDate
import com.shiftboard.schedulepro.core.network.model.notification.BulkMessaging
import com.shiftboard.schedulepro.core.network.model.notification.TradeRequestUpdated
import com.shiftboard.schedulepro.core.network.model.notification.TradeRequested
import com.shiftboard.schedulepro.core.persistence.Converters
import com.shiftboard.schedulepro.databinding.NotificationsFragmentBinding
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.OffsetDateTime

class NotificationsFragment: BottomNavFragment() {
    override val titleRes: Int = R.string.notifications
    override val layoutRes: Int = R.layout.notifications_fragment

    private val boundView by lazyViewBinding<NotificationsFragmentBinding>()
    private val viewModel by sharedViewModel<NotificationViewModel>()
    private val args by navArgs<NotificationsFragmentArgs>()

    private val adapter by lazy {
        NotificationAdapter {
            if (it.type == NotificationConstants.BULK_MESSAGE) {
                MessageDetailsFragment(it.content as BulkMessaging, it.sentDateUtc).show(childFragmentManager,"MESSAGE_DETAILS")
                sharedState.markNotificationRead(it.id)
            }
            if (it.type == NotificationConstants.TRADE_REQUESTED) {
                sharedState.markNotificationRead(it.id)
                TradeDetailsFragment((it.content as TradeRequested).tradeId) { from, to, message -> }.show(childFragmentManager,"MESSAGE_DETAILS")
            }

            if (it.type == NotificationConstants.TRADE_REQUEST_UPDATED ) {
                sharedState.markNotificationRead(it.id)
                TradeDetailsFragment((it.content as TradeRequestUpdated).tradeId){ from, to, message -> }.show(childFragmentManager,"MESSAGE_DETAILS")
            }
            val focusDate = it.focusDate() ?: return@NotificationAdapter

            sharedState.markNotificationRead(it.id)

            tryWithNav {
                navigate(
                    HomeNavGraphDirections.actionGlobalSchedule()
                        .setStartDate(focusDate.toString()))
            }
        }
    }

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.Notifications

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.notificationData.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryWith(boundView) {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            // This is dumb and I don't really like maintaining the scroll state for this page
            adapter.addLoadStateListener {
                if (it.source.refresh is LoadState.NotLoading) {
                    if (adapter.itemCount > 0) {
                        try {
                            (recyclerView.layoutManager as LinearLayoutManager)
                                .scrollToPositionWithOffset(viewModel.scrollState, 0)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            recyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    viewModel.scrollState =
                        (recyclerView.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                            ?: 0
                }
            })

            if (args.selectedNotificationId != null) {
                val b = Converters().messageContentAdapter.fromJson(args.selectedNotificationId!!)
                MessageDetailsFragment(b!!, OffsetDateTime.now()).show(childFragmentManager,"MESSAGE_DETAILS")
            }
        }
    }
}