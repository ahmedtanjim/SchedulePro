package com.shiftboard.schedulepro.content.notifications.ui

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.common.analytics.BindingViewHolder
import com.shiftboard.schedulepro.core.common.analytics.inflate
import com.shiftboard.schedulepro.core.common.utils.modifiedTimestamp
import com.shiftboard.schedulepro.core.messaging.MessagingTemplates
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import com.shiftboard.schedulepro.databinding.NotificationsElementBinding
import com.shiftboard.schedulepro.ui.dp

class NotificationAdapter(private val onNotificationClick: OnNotificationClick) :
    PagingDataAdapter<Notification, NotificationAdapter.NotificationViewHolder>(
        object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(
                oldItem: Notification,
                newItem: Notification,
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Notification,
                newItem: Notification,
            ): Boolean =
                oldItem == newItem
        }
    ) {

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(parent).apply {
            itemView.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { getItem(it) }
                    ?.let { onNotificationClick.onClick(it) }
            }
        }
    }

    class NotificationViewHolder(parent: ViewGroup) :
        BindingViewHolder<NotificationsElementBinding>(
            NotificationsElementBinding.bind(parent.inflate(R.layout.notifications_element))
        ) {
        fun bind(notification: Notification) {
            viewBinding.title.text = MessagingTemplates.notificationTitle(itemView.context, notification.type, notification.content)
            viewBinding.content.text = MessagingTemplates.parseMessage(itemView.context, notification.type, notification.content)
            viewBinding.timestamp.text = notification.sentDateUtc.modifiedTimestamp(AppPrefs.militaryTime)

            if (notification.notificationRead) {
                viewBinding.contentCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.very_light_gray))
                viewBinding.contentCard.cardElevation = 0f
            } else {
                viewBinding.contentCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
                viewBinding.contentCard.cardElevation = 4.dp.toFloat()
            }
        }
    }

    fun interface OnNotificationClick {
        fun onClick(notification: Notification)
    }
}