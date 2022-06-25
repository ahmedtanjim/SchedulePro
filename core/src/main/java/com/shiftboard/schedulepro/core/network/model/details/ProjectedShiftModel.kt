package com.shiftboard.schedulepro.core.network.model.details

import android.graphics.Color
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.details.ProjectedShiftDetails
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class ProjectedShiftModel(
    val hoursPaid: Float,
    val overtimeMultiplier: Float,
    val overtimeHours: Float,
    val regularOvertimeHours: Float,
    val date: LocalDate,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeId: String,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    val startsOnPreviousDay: Boolean,
    val startsOnNextDay: Boolean,
    val endsOnPreviousDay: Boolean,
    val endsOnNextDay: Boolean,
    val positionId: String,
    val positionCode: String,
    val positionDescription: String,
    val locationId: String,
    val locationCode: String,
    val locationDescription: String,
    val color: String,
    val actions: List<String>,
) : Mappable<ProjectedShiftDetails> {
    override fun map(cachedAt: OffsetDateTime): ProjectedShiftDetails {
        return ProjectedShiftDetails(
            hoursPaid = hoursPaid,
            overtimeMultiplier = overtimeMultiplier,
            overtimeHours = overtimeHours,
            regularOvertimeHours = regularOvertimeHours,
            date = date,
            startTime = startTime,
            endTime = endTime,
            shiftTimeId = shiftTimeId,
            shiftTimeCode = shiftTimeCode,
            shiftTimeDescription = shiftTimeDescription,
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
            actions = actions,
            cachedAt = cachedAt
        )
    }
}