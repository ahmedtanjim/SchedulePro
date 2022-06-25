package com.shiftboard.schedulepro.core.network.model.group

import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class ScheduleItemCollection (
    val date: OffsetDateTime,
    val items: List<Item>,
    val actions: List<Any?>
){
    fun toDayDate(): String {
        return this.date.format(DateTimeFormatter.ofPattern("EEEE dd"))
    }
}