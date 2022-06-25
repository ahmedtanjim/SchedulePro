package com.shiftboard.schedulepro.core.network.model.notification

import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class NotificationModel(
    val id: String,
    val type: String,
    val notificationRead: Boolean,
    val content: NotificationContent,
    val lastModifiedUtc: OffsetDateTime,
    val sentDateUtc: OffsetDateTime,
) : Mappable<Notification> {
    override fun map(cachedAt: OffsetDateTime): Notification {
        return Notification(
            id = id,
            type = type,
            notificationRead = notificationRead,
            sentDateUtc = sentDateUtc,
            lastModifiedUtc = lastModifiedUtc,
            content = content,
            cachedAt = cachedAt,
        )
    }
}