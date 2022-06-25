package com.shiftboard.schedulepro.core.network.model.details

import android.graphics.Color
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.network.model.group.Item
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.details.ShiftDetails
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class ShiftDetailsModel(
    val id: String,
    val hoursPaid : Float,
    val regularOvertimeHours : Float,
    val overtimeMultiplier : Float,
    val overtimeHours : Float,
    val comments : String,
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
    val color : String,
//    val type : 0,
    val date : LocalDate,
    val shiftTimeId : String,
    val shiftTimeCode : String,
    val shiftTimeDescription : String,
    val actions : List<String>
): Mappable<ShiftDetails> {
    override fun map(cachedAt: OffsetDateTime): ShiftDetails {
        return ShiftDetails(
            id = id,
            hoursPaid = hoursPaid,
            regularOvertimeHours = regularOvertimeHours,
            overtimeMultiplier = overtimeMultiplier,
            overtimeHours = overtimeHours,
            comments = comments,
            startTime = startTime,
            endTime = endTime,
            startsOnPreviousDay = startsOnPreviousDay,
            startsOnNextDay = startsOnNextDay,
            endsOnPreviousDay = endsOnPreviousDay,
            endsOnNextDay = endsOnNextDay,
            positionId = positionId,
            positionCode = positionCode,
            positionDescription = positionDescription,
            locationId = locationId,
            locationCode = locationCode,
            locationDescription = locationDescription,
            color = parseAsColor(color),
            date = date,
            typeId = shiftTimeId,
            typeCode = shiftTimeCode,
            typeDescription = shiftTimeDescription,
            actions = actions,
            cachedAt = cachedAt
        )
    }
    fun toItem() : Item {
        return Item(
            id = id,
            color = color,
            shiftTimeCode = shiftTimeCode,
            positionID = positionId,
            locationID = locationId,
            positionCode = positionCode,
            locationCode = locationCode,
            startTime = startTime,
            endTime = endTime,
            date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
            type = "",
            shiftTimeID = shiftTimeId,
            shiftTimeDescription = shiftTimeDescription,
            leaveTypeId = null,
            leaveTypeCode = null,
            leaveSpansAllDay = null,
            leaveTypeDescription = null,
            startsOnNextDay = startsOnNextDay,
            startsOnPreviousDay = startsOnPreviousDay,
            endsOnPreviousDay = endsOnPreviousDay,
            endsOnNextDay = endsOnNextDay,
            positionDescription = positionDescription,
            locationDescription = locationDescription,
            regularOvertimeHours = regularOvertimeHours.toLong(),
            actions = actions
        )
    }
}