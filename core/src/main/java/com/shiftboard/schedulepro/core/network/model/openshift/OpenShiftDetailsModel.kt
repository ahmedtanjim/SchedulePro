package com.shiftboard.schedulepro.core.network.model.openshift

import com.shiftboard.schedulepro.core.network.adapters.AndroidColor
import com.shiftboard.schedulepro.core.network.model.details.ShiftDetailsModel
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime



@JsonClass(generateAdapter = true)
data class OpenShiftDetailsCollection (
    val openShifts : List<OpenShiftDetails>
) : Mappable<List<OpenShiftDetails>> {
    override fun map(cachedAt: OffsetDateTime): List<OpenShiftDetails> {
        return openShifts
    }
}

@JsonClass(generateAdapter = true)
data class OpenShiftDetails (
    val id: String,
    val shiftRequestId: String?,
    val status: String,
    val shift: OpenShiftInnerDetails,
    val shiftRequestStatus: String,
    val totalRequests: Int,
    val openShiftType: String,
    val actions: List<String>,
    var selected: Boolean = false
)

@JsonClass(generateAdapter = true)
data class OpenShiftInnerDetails (
    val hoursPaid: Double,
    val overtimeMultiplier: Double,
    val overtimeHours: Double,
    val type: String,
    val id: String,
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

    @AndroidColor val color: Int,
    val regularOvertimeHours: Double
)

@JsonClass(generateAdapter = true)
data class PostOpenShiftResponse(
    val id: String,
    val status: String?,
    val openShiftId: String?,
    val lastModified: String
)

