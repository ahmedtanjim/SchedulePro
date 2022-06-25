package com.shiftboard.schedulepro.core.persistence.model.details

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity
data class LeaveDetails(
    @PrimaryKey val id: String,
    val hoursPaid : Float ,
    val regularOvertimeHours : Float,
    val overtimeMultiplier : Float,
    val overtimeHours : Float,
    val comments : String,
    val startTime : OffsetDateTime,
    val endTime : OffsetDateTime,
    val positionId : String,
    val positionCode : String,
    val positionDescription : String,
    val locationId: String,
    val locationCode : String,
    val locationDescription : String,
    val color : Int,
//    val type : 0,
    val date : LocalDate,
    val typeId : String,
    val typeCode : String,
    val typeDescription : String,
    val leaveRequestDetailId: String?,
    val cachedAt: OffsetDateTime
)