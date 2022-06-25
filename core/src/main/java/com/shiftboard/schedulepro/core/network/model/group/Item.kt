package com.shiftboard.schedulepro.core.network.model.group

import com.shiftboard.schedulepro.core.common.utils.DateUtils
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class Item (
    val type: String,
    val id: String?,
    val date: String,
    val startTime: OffsetDateTime?,
    val endTime: OffsetDateTime?,
    val shiftTimeID: String?,
    val shiftTimeCode: String?,
    val shiftTimeDescription: String?,
    val leaveTypeDescription: String?,
    val leaveSpansAllDay: Boolean?,
    val leaveTypeId: String?,
    val leaveTypeCode: String?,
    val startsOnPreviousDay: Boolean?,
    val startsOnNextDay: Boolean?,
    val endsOnPreviousDay: Boolean?,
    val endsOnNextDay: Boolean?,
    val positionID: String?,
    val positionCode: String?,
    val positionDescription: String?,
    val locationID: String?,
    val locationCode: String?,
    val locationDescription: String?,
    val color: String = "#000000",
    val regularOvertimeHours: Long?,
    val actions: List<String> = listOf()
){
    fun toTradeShift() : TradeShift {
        return TradeShift(
            id = id ?: "",
            Color = color,
            shiftCode = shiftTimeCode?:"",
            positionCode = positionCode?:"",
            locationCode = locationCode?:"",
            startTime = startTime ?: OffsetDateTime.MIN,
            endTime = endTime ?: OffsetDateTime.MIN,
            shiftDate = DateUtils.parseLocalDate(date)!!

        )
    }
}