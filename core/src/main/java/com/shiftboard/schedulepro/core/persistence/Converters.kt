package com.shiftboard.schedulepro.core.persistence

import androidx.room.TypeConverter
import com.shiftboard.schedulepro.core.messaging.NotificationConstants
import com.shiftboard.schedulepro.core.network.model.notification.BulkMessaging
import com.shiftboard.schedulepro.core.network.model.notification.NotificationContent
import com.shiftboard.schedulepro.core.network.model.notification.TradeRequestUpdated
import com.shiftboard.schedulepro.core.network.model.notification.TradeRequested
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.koin.core.KoinComponent
import org.koin.core.get
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter


class Converters: KoinComponent {
    @TypeConverter
    fun offsetDateTimeToTimestamp(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it) }
    }

    @TypeConverter
    fun nullableTimestampToOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @TypeConverter
    fun localDateToTimestamp(value: String): LocalDate {
        return LocalDate.parse(value)
    }

    @TypeConverter
    fun timestampToLocalDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun stringListToString(list: List<String>): String {
        return list.joinToString(separator = "|")
    }

    @TypeConverter
    fun stringToStringList(input: String): List<String> {
        return input.split("|")
    }

    val mapAdapter by lazy {
        Moshi.Builder().build()
            .adapter<Map<String, String>>(Types.newParameterizedType(Map::class.java, String::class.java, String::class.java))
    }

    @TypeConverter
    fun stringMapToString(input: Map<String, String>): String {
        return mapAdapter.toJson(input)
    }

    @TypeConverter
    fun stringToStringMap(input: String): Map<String, String> {
        return mapAdapter.fromJson(input) ?: mapOf()
    }

    val notificationContentAdapter: JsonAdapter<NotificationContent> by lazy {
        get<Moshi>().adapter(NotificationContent::class.java)
    }

    val messageContentAdapter: JsonAdapter<BulkMessaging> by lazy {
        get<Moshi>().adapter(BulkMessaging::class.java)
    }

    val tradeContentAdapter: JsonAdapter<TradeRequested> by lazy {
        get<Moshi>().adapter(TradeRequested::class.java)
    }

    val tradeUpdateContentAdapter: JsonAdapter<TradeRequestUpdated> by lazy {
        get<Moshi>().adapter(TradeRequestUpdated::class.java)
    }

    @TypeConverter
    fun notificationContentToString(input: NotificationContent): String {
        if (input.type == NotificationConstants.BULK_MESSAGE) return messageContentAdapter.toJson((input as BulkMessaging))
        return notificationContentAdapter.toJson(input)
    }

    @TypeConverter
    fun stringToNotificationContent(input: String): NotificationContent {
        if (input.contains(NotificationConstants.BULK_MESSAGE)) {
            return messageContentAdapter.fromJson(input)!!
        }
        return notificationContentAdapter.fromJson(input)!!
    }
}