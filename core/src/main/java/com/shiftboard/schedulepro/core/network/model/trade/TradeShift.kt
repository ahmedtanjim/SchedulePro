package com.shiftboard.schedulepro.core.network.model

import com.shiftboard.schedulepro.core.network.model.group.Item
import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@JsonClass(generateAdapter = true)

data class TradeShiftCollection (
    val tradeShifts: List<TradeShift>
    )

@JsonClass(generateAdapter = true)
data class TradeShift (
    val id: String,
    val Color: String?,
    val shiftCode: String,
    val positionCode: String,
    val locationCode: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftDate: org.threeten.bp.LocalDate,
){
    fun toItem() : Item {
        return Item(
            id = id,
            color = Color!!,
            shiftTimeCode = shiftCode,
            positionID = positionCode,
            locationID = locationCode,
            positionCode = positionCode,
            locationCode = locationCode,
            startTime = startTime,
            endTime = endTime,
            date = shiftDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            type = "",
            shiftTimeID = null,
            shiftTimeDescription = null,
            leaveTypeId = null,
            leaveTypeCode = null,
            leaveSpansAllDay = null,
            leaveTypeDescription = null,
            startsOnNextDay = null,
            startsOnPreviousDay = null,
            endsOnPreviousDay = null,
            endsOnNextDay = null,
            positionDescription = null,
            locationDescription = null,
            regularOvertimeHours = null,
            actions = listOf()
        )
    }
}


@JsonClass(generateAdapter = true)
data class TradeDetailsElement(
    val id: String,
    val originEmployee: Employee,
    val recipientEmployee: Employee,
    val originShifts: List<TradeShift>,
    val recipientShifts: List<TradeShift>,
    val actions: List<String>
)
