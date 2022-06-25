package com.shiftboard.schedulepro.core.network.model.schedule

import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.core.network.adapters.AndroidColor
import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


interface ScheduleItemsModel {
    val type: String
    val date: LocalDate
}

object ScheduleErrorElement : ScheduleItemsModel {
    override val date: LocalDate = LocalDate.MIN
    override val type: String = "ERROR"
}

@JsonClass(generateAdapter = true)
data class NoneElementModel(
    override val date: LocalDate,
    override val type: String, // NotScheduled

    val description: String,
) : ScheduleItemsModel

@JsonClass(generateAdapter = true)
data class ProjectedLeaveElementModel(
    override val date: LocalDate,
    override val type: String, // ProjectedLeave

    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,

    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,

    @AndroidColor val color: Int,
) : ScheduleItemsModel, Mappable<ProjectedLeaveElement> {
    override fun map(cachedAt: OffsetDateTime): ProjectedLeaveElement {
        return ProjectedLeaveElement(
            date = date,
            startTime = startTime,
            endTime = endTime,
            leaveTypeId = leaveTypeId,
            leaveTypeCode = leaveTypeCode,
            leaveTypeDescription = leaveTypeDescription,
            color = color,
            cachedAt = cachedAt
        )
    }
}

@JsonClass(generateAdapter = true)
data class ProjectedShiftElementModel(
    override val date: LocalDate,
    override val type: String, // ProjectedShift

    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,

    val shiftTimeId: String,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,

    val positionId: String?,
    val positionCode: String?,
    val positionDescription: String?,

    val locationId: String?,
    val locationCode: String?,
    val locationDescription: String?,

    @AndroidColor val color: Int,

    val startsOnPreviousDay: Boolean,
    val startsOnNextDay: Boolean,
    val endsOnPreviousDay: Boolean,
    val endsOnNextDay: Boolean,

    val regularOvertimeHours: Float,

    val actions: List<String>,
) : ScheduleItemsModel, Mappable<ProjectedShiftElement> {
    override fun map(cachedAt: OffsetDateTime): ProjectedShiftElement {
        return ProjectedShiftElement(
            date = date,
            startTime = startTime,
            endTime = endTime,
            shiftTimeId = shiftTimeId,
            shiftTimeCode = shiftTimeCode,
            shiftTimeDescription = shiftTimeDescription,
            positionId = positionId,
            positionCode = positionCode,
            positionDescription = positionDescription,
            locationId = locationId,
            locationCode = locationCode,
            locationDescription = locationDescription,
            color = color,
            startsOnPreviousDay = startsOnPreviousDay,
            startsOnNextDay = startsOnNextDay,
            endsOnPreviousDay = endsOnPreviousDay,
            endsOnNextDay = endsOnNextDay,
            regularOvertimeHours = regularOvertimeHours,
            actions = actions,
            cachedAt = cachedAt,
        )
    }
}


@JsonClass(generateAdapter = true)
data class HolidayElementModel(
    override val date: LocalDate,
    override val type: String, // Holiday

    val name: String,
) : ScheduleItemsModel


@JsonClass(generateAdapter = true)
data class SignupElementModel(
    override val date: LocalDate,
    override val type: String, // SignUps
    val id: String,
    val signUps: List<SignupEventModel>,
) : ScheduleItemsModel, Mappable<SignupWithEvents> {
    override fun map(cachedAt: OffsetDateTime): SignupWithEvents {
        return SignupWithEvents(
            SignupElement(
                date, id, cachedAt
            ),
            signUps.map {
                SignupEvent(
                    id, it.startTime, it.endTime,
                    it.shiftTimeCode, parseAsColor(it.color)
                )
            }
        )
    }
}

@JsonClass(generateAdapter = true)
data class SignupEventModel(
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val color: String,
    val shiftTimeCode: String,
)

@JsonClass(generateAdapter = true)
data class ShiftElementModel(
    override val date: LocalDate,
    override val type: String, // Shift

    val id: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeId: String,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    val startsOnPreviousDay: Boolean,
    val startsOnNextDay: Boolean,
    val endsOnPreviousDay: Boolean,
    val endsOnNextDay: Boolean,

    val positionId: String?,
    val positionCode: String?,
    val positionDescription: String?,

    val locationId: String?,
    val locationCode: String?,
    val locationDescription: String?,
    val regularOvertimeHours: Float,
    val color: String,
    val comments: String? = null,
) : ScheduleItemsModel, Mappable<ShiftElement> {
    override fun map(cachedAt: OffsetDateTime): ShiftElement {
        return ShiftElement(
            id = id,
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
            regularOvertimeHours = regularOvertimeHours,
            comments = comments ?: "",
            cachedAt = cachedAt,
        )
    }
}

@JsonClass(generateAdapter = true)
data class OpenShiftElementModel(
    override val date: LocalDate,
    override val type: String, // OpenShift
    val openShiftRequests: List<OpenShiftElementChildModel>
) : ScheduleItemsModel, Mappable<OpenShiftWithEvents> {
    override fun map(cachedAt: OffsetDateTime): OpenShiftWithEvents {
        return OpenShiftWithEvents(OpenShiftElement(date, "openShift${date}", cachedAt), openShiftRequests.map {
                OpenShiftEvent(it.id,
                    "openShift${date}",
                    date,
                    it.startTime,
                    it.endTime,
                    it.shiftTimeCode,
                    it.shiftTimeDescription,
                    it.positionCode,
                    it.positionDescription,
                    it.locationCode,
                    it.locationDescription,
                    parseAsColor(it.color),
                    it.status,
                    it.requestStatus,
                    cachedAt)})
    }
}
@JsonClass(generateAdapter = true)
data class OpenShiftElementChildModel(
    val id: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    val positionCode: String?,
    val positionDescription: String?,
    val locationCode: String?,
    val locationDescription: String?,
    val color: String,
    val status: String?,
    val requestStatus: String?
)

@JsonClass(generateAdapter = true)
data class LeaveElementModel(
    override val date: LocalDate,
    override val type: String, // Leave

    val id: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,
    val color: String,
    val requestStatus: RequestStatus?,
    val comments: String? = null,
) : ScheduleItemsModel, Mappable<LeaveElement> {
    override fun map(cachedAt: OffsetDateTime): LeaveElement {
        return LeaveElement(
            id = id,
            date = date,
            startTime = startTime,
            endTime = endTime,
            leaveTypeId = leaveTypeId,
            leaveTypeCode = leaveTypeCode,
            requestStatus = requestStatus?.status,
            leaveTypeDescription = leaveTypeDescription,
            color = parseAsColor(color),
            comments = comments ?: "",
            cachedAt = cachedAt,
        )
    }
}

@JsonClass(generateAdapter = true)
data class RequestStatus(val status: String)


@JsonClass(generateAdapter = true)
data class PendingLeaveElementModel(
    override val date: LocalDate,
    override val type: String, // Leave

    val id: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val leaveTypeId: String,
    val leaveTypeCode: String,
    val leaveTypeDescription: String,
    val color: String,
    val status: String,

    ) : ScheduleItemsModel, Mappable<PendingLeaveElement> {
    override fun map(cachedAt: OffsetDateTime): PendingLeaveElement {
        return PendingLeaveElement(
            id = id,
            date = date,
            startTime = startTime,
            endTime = endTime,
            leaveTypeId = leaveTypeId,
            leaveTypeCode = leaveTypeCode,
            leaveTypeDescription = leaveTypeDescription,
            status = status,
            color = parseAsColor(color),
            cachedAt = cachedAt,
        )
    }
}




@JsonClass(generateAdapter = true)
data class TradeElementModel(
    override val date: LocalDate,
    override val type: String, // Trade
    val trades: List<TradeElementChildModel>
) : ScheduleItemsModel, Mappable<TradeEvent> {
    override fun map(cachedAt: OffsetDateTime): TradeEvent {
        return trades.map {
            TradeEvent(
                it.id,
                date,
                it.status,
                it.originatorName,
                it.recipientName,
                it.isOriginator,
                cachedAt
            )
        }.first()
    }
}
@JsonClass(generateAdapter = true)
data class TradeElementChildModel(
    val id: String,
    val status: Int,
    val originatorName: String,
    val recipientName: String,
    val isOriginator: Boolean
)
