package com.shiftboard.schedulepro.core.persistence.model.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shiftboard.schedulepro.core.network.model.notification.NotificationContent
import org.threeten.bp.OffsetDateTime


@Entity
data class Notification(
    @PrimaryKey val id: String,
    val type: String,
    val notificationRead: Boolean,
    val content: NotificationContent,
    val lastModifiedUtc: OffsetDateTime,
    val sentDateUtc: OffsetDateTime,
    val cachedAt: OffsetDateTime
)