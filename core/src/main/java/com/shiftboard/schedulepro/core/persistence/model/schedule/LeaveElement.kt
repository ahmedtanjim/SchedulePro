package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.Entity
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


@Entity(primaryKeys = ["date", "id"])
data class LeaveElement(
    override val date: LocalDate,

    val id: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,

    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,

    val color: Int,
    val comments: String,
    val requestStatus: String?,
    val cachedAt: OffsetDateTime,
) : ScheduleItem


@Entity(primaryKeys = ["date", "id"])
data class PendingLeaveElement(
    override val date: LocalDate,
    val id: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,

    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,

    val color: Int,
    val status: String,

    val cachedAt: OffsetDateTime,
) : ScheduleItem

@Entity(primaryKeys = ["date", "leaveTypeId"])
data class ProjectedLeaveElement(
    override val date: LocalDate,

    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,

    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,

    val color: Int,

    val cachedAt: OffsetDateTime,
) : ScheduleItem