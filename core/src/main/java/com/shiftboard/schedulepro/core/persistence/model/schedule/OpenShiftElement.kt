package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(
    primaryKeys = ["date", "id"]
)
data class OpenShiftElement(
    override val date: LocalDate,
    val id: String,

    val cachedAt: OffsetDateTime,
): ScheduleItem

data class OpenShiftDetailsElement(
    val id: String,
    val date: LocalDate,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    val positionCode: String?,
    val positionDescription: String?,
    val locationCode: String?,
    val locationDescription: String?,
    val color: Int,
    val status: String?,
    val requestStatus: String?,
    val cachedAt: OffsetDateTime
)

data class OpenShiftWithEvents(
    @Embedded val openShift: OpenShiftElement,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentID"
    )
    val events: List<OpenShiftEvent>
): ScheduleItem {
    override val date: LocalDate
        get() = openShift.date
}

@Entity(
    primaryKeys = ["id"])
data class OpenShiftEvent(
    val id: String,
    val parentID: String,
    val date: LocalDate,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    val positionCode: String?,
    val positionDescription: String?,
    val locationCode: String?,
    val locationDescription: String?,
    val color: Int,
    val status: String?,
    val requestStatus: String?,
    val cachedAt: OffsetDateTime
)