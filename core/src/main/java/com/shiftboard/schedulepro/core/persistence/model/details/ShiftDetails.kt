package com.shiftboard.schedulepro.core.persistence.model.details

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shiftboard.schedulepro.core.persistence.Cached
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity
data class ShiftDetails(
    @PrimaryKey val id: String,
    val hoursPaid : Float,
    val regularOvertimeHours : Float,
    val overtimeMultiplier : Float,
    val overtimeHours : Float,
    val startTime : OffsetDateTime,
    val endTime : OffsetDateTime,
    val startsOnPreviousDay : Boolean,
    val startsOnNextDay : Boolean,
    val endsOnPreviousDay : Boolean,
    val endsOnNextDay : Boolean,
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
    val actions: List<String>,

    val comments : String,

    override val cachedAt: OffsetDateTime
): Cached