package com.shiftboard.schedulepro.core.network.adapters

import android.graphics.Color
import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.network.model.notification.*
import com.squareup.moshi.*
import com.squareup.moshi.internal.Util
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


class LocalDateTimeAdapter {
    @ToJson
    fun toJson(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    @FromJson
    fun fromJson(value: String): LocalDateTime? {
        return DateUtils.parseLocalDateTime(value)
    }
}

class OffsetDateTimeAdapter {
    @ToJson
    fun toJson(value: OffsetDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @FromJson
    fun fromJson(value: String): OffsetDateTime? {
        return DateUtils.parseOffsetDateTime(value)
    }
}

class LocalDateAdapter {
    @ToJson
    fun toJson(value: LocalDate?): String? {
        return value?.format(DateTimeFormatter.ISO_DATE)
    }

    @FromJson
    fun fromJson(value: String): LocalDate? {
        return DateUtils.parseLocalDate(value)
    }
}

class NotificationAdapter(val moshi: Moshi) : JsonAdapter<NotificationModel>() {
    companion object {
        val INSTANCE = NotificationAdapterFactory()
    }

    private val options: JsonReader.Options = JsonReader.Options.of("id",
        "type",
        "notificationRead",
        "content",
        "lastModifiedUtc",
        "sentDateUtc")

    private val stringAdapter: JsonAdapter<String> = moshi.adapter(String::class.java,
        emptySet(),
        "id")
    private val booleanAdapter: JsonAdapter<Boolean> = moshi.adapter(Boolean::class.java,
        emptySet(),
        "notificationRead")
    private val offsetDateTimeAdapter: JsonAdapter<OffsetDateTime> =
        moshi.adapter(OffsetDateTime::class.java,
            emptySet(),
            "lastModified")

    override fun fromJson(reader: JsonReader): NotificationModel {
        var id: String? = null
        var type: String? = null
        var notificationRead: Boolean? = null
        var content: NotificationContent? = null
        var lastModifiedUtc: OffsetDateTime? = null
        var sentDateUtc: OffsetDateTime? = null

        // We need to know the type before anything else
        val peekReader = reader.peekJson()
        peekReader.isLenient = true
        peekReader.setFailOnUnknown(false)
        peekReader.beginObject()
        while (peekReader.hasNext()) {
            when (peekReader.selectName(JsonReader.Options.of("type"))) {
                0 -> {
                    type = stringAdapter.fromJson(peekReader) ?: throw Util.unexpectedNull("type",
                        "type",
                        peekReader)
                }
                -1 -> {
                    peekReader.skipName()
                    peekReader.skipValue()
                }
            }
        }
        peekReader.endObject()

        if (type == null) {
            throw IOException("Missing notification type")
        }

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.selectName(options)) {
                0 -> id = stringAdapter.fromJson(reader) ?: throw Util.unexpectedNull("id",
                    "id",
                    reader)
                1 -> type = stringAdapter.fromJson(reader) ?: throw Util.unexpectedNull("type",
                    "type",
                    reader)
                2 -> notificationRead =
                    booleanAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                        "notificationRead",
                        "notificationRead",
                        reader)

                3 -> content = moshi.notificationAdapterForType(type!!).fromJson(reader)
                    ?: throw Util.unexpectedNull("content", "content", reader)

                4 -> lastModifiedUtc =
                    offsetDateTimeAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                        "lastModifiedUtc",
                        "lastModifiedUtc",
                        reader)
                5 -> sentDateUtc =
                    offsetDateTimeAdapter.fromJson(reader) ?: throw Util.unexpectedNull(
                        "sentDateUtc",
                        "sentDateUtc",
                        reader)
                -1 -> {
                    // Unknown name, skip it.
                    reader.skipName()
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        return NotificationModel(
            id = id ?: throw Util.missingProperty("id", "id", reader),
            type = type ?: throw Util.missingProperty("type", "type", reader),
            notificationRead = notificationRead ?: throw Util.missingProperty("notificationRead",
                "notificationRead", reader),
            content = content ?: throw Util.missingProperty("content", "content", reader),
            lastModifiedUtc = lastModifiedUtc ?: throw Util.missingProperty("lastModifiedUtc",
                "lastModifiedUtc",
                reader),
            sentDateUtc = sentDateUtc ?: throw Util.missingProperty("sentDateUtc",
                "sentDateUtc",
                reader)
        )
    }

    override fun toJson(writer: JsonWriter, value: NotificationModel?) {
        throw UnsupportedOperationException("NotificationAdapter is only used to deserialize objects")
    }

    class NotificationAdapterFactory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            if (Types.equals(type, NotificationModel::class.java)) {
                return NotificationAdapter(moshi)
            }
            return null
        }
    }
}

fun Moshi.notificationAdapterForType(type: String): JsonAdapter<out NotificationContent> =
    when (type) {
        "LeaveRequestStatusUpdated" -> adapter(LeaveRequestStatusUpdatedContent::class.java)
        "LeaveRequestCancellationRequestStatusUpdated" -> adapter(LeaveRequestCancellationContent::class.java)
        "LeaveRequestAmendmentRequestStatusUpdated" -> adapter(LeaveRequestAmendmentContent::class.java)
        "LeaveRequestDetailsUpdated" -> adapter(LeaveRequestDetailsContent::class.java)
        "SchedulePublished" -> adapter(SchedulePublishedContent::class.java)
        "ShiftEdited" -> adapter(SingleOptionalShiftNotification::class.java)
        "ShiftAssignedOrUnassigned" -> adapter(SingleOptionalShiftNotification::class.java)
        "SingleOptionalShiftNotification" -> adapter(SingleOptionalShiftNotification::class.java)
        "BulkShiftsEdited" -> adapter(BulkNotification::class.java)
        "BulkShiftsAssignedOrUnassigned" -> adapter(BulkNotification::class.java)
        "BulkNotification" -> adapter(BulkNotification::class.java)
        "BulkMessaging" -> adapter(BulkMessaging::class.java)
        "OpenShiftsPublished" -> adapter(OpenShiftNotification::class.java)
        "OpenShiftRequestStatusUpdated" -> adapter(OpenShiftNotification::class.java)
        "TurndownPosterNoTakers" -> adapter(TurndownPosterNoTakersNotification::class.java)
        "TurndownPosterReassigned" -> adapter(TurndownPosterReassignedNotification::class.java)
        "TradeRequested" -> adapter(TradeRequested::class.java)
        "TradeRequestUpdated" -> adapter(TradeRequestUpdated::class.java)
        else -> adapter(InvalidContent::class.java)
    }

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class AndroidColor

class AndroidColorAdapter : JsonAdapter<Int>() {
    companion object {
        val INSTANCE = AndroidColorAdapterFactory()
    }

    override fun fromJson(reader: JsonReader): Int? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> parseAsColor(reader.nextString())
            else -> {
                reader.skipValue()
                Color.WHITE
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Int?) {
        throw UnsupportedOperationException("AndroidColorAdapter is only used to deserialize objects")
    }

    class AndroidColorAdapterFactory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            Types.nextAnnotations(annotations, AndroidColor::class.java) ?: return null
            return AndroidColorAdapter()
        }

    }
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class JsonMap

class MapAdapter : JsonAdapter<Map<String, String?>>() {
    companion object {
        val INSTANCE = MapAdapterFactory()
    }

    override fun fromJson(reader: JsonReader): Map<String, String?> {
        if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
            reader.beginObject()
            val map = hashMapOf<String, String?>()
            var token = ""
            while (true) {
                when (reader.peek()) {
                    JsonReader.Token.END_OBJECT ->
                        break
                    JsonReader.Token.BEGIN_OBJECT -> throw Exception("nested object in json map is not allowed")
                    JsonReader.Token.BEGIN_ARRAY -> throw Exception("nested object in json map is not allowed")
                    JsonReader.Token.END_DOCUMENT -> throw Exception("unexpected document end")
                    JsonReader.Token.BOOLEAN -> {
                        val value = reader.nextBoolean()
                        map[token] = value.toString()
                    }
                    JsonReader.Token.NULL -> {
                        reader.skipValue()
                        map[token] = null
                    }
                    JsonReader.Token.STRING -> {
                        val value = reader.nextString()
                        map[token] = value
                    }
                    JsonReader.Token.NUMBER -> {
                        val value = reader.nextDouble()
                        map[token] = value.toString()
                    }
                    JsonReader.Token.NAME -> {
                        token = reader.nextName()
                    }
                }
            }
            reader.endObject()
            return map
        } else {
            reader.skipValue()
            return mapOf()
        }
    }

    override fun toJson(writer: JsonWriter, value: Map<String, String?>?) {
        throw UnsupportedOperationException("MapAdapter is only used to deserialize objects")
    }

    class MapAdapterFactory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            Types.nextAnnotations(annotations, JsonMap::class.java) ?: return null
            return MapAdapter()
        }
    }
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class ForceBoolean

// If the next value is true || (lower) == "true" || 1 || "1" return true otherwise return false
// Servola is dumb and will give boolean values back to us in a bunch of different ways so we have to
// normalize them for sanity sake
class ForceToBooleanAdapter : JsonAdapter<Boolean>() {
    companion object {
        val INSTANCE = ForceToBooleanAdapterFactory()
    }

    override fun fromJson(reader: JsonReader): Boolean? {
        return when (reader.peek()) {
            JsonReader.Token.NUMBER -> reader.nextInt() == 1
            JsonReader.Token.STRING -> reader.nextString() == "1" || reader.nextString()
                .toLowerCase(
                    Locale.ROOT) == "true"
            JsonReader.Token.BOOLEAN -> reader.nextBoolean()
            else -> {
                reader.skipValue()
                false
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Boolean?) {
        throw UnsupportedOperationException("ForceToBooleanAdapter is only used to deserialize objects")
    }

    class ForceToBooleanAdapterFactory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            Types.nextAnnotations(annotations, ForceBoolean::class.java) ?: return null
            return ForceToBooleanAdapter()
        }

    }
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class ForceString


class ForceToStringAdapter : JsonAdapter<String>() {
    companion object {
        val INSTANCE = ForceToStringAdapterFactory()
    }

    class ForceToStringAdapterFactory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            Types.nextAnnotations(annotations, ForceString::class.java) ?: return null
            return ForceToStringAdapter()
        }

    }

    override fun fromJson(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString()
            JsonReader.Token.NUMBER -> reader.nextString()
            JsonReader.Token.BOOLEAN -> reader.nextBoolean().toString()
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: String?) {
        throw UnsupportedOperationException("ForceToBooleanAdapter is only used to deserialize objects")
    }
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class StringOrNull

// Only read the value if it is a json string otherwise return null
class StringOrNullAdapter : JsonAdapter<String>() {
    companion object {
        val INSTANCE = StringOrNullAdapterFactory()
    }

    override fun fromJson(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString()
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: String?) {
        throw UnsupportedOperationException("StringOrNullAdapter is only used to deserialize objects")
    }

    class StringOrNullAdapterFactory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi,
        ): JsonAdapter<*>? {
            Types.nextAnnotations(annotations, StringOrNull::class.java) ?: return null
            return StringOrNullAdapter()
        }

    }
}