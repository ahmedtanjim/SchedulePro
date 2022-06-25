package com.shiftboard.schedulepro.core.network.model.schedule

import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


@JsonClass(generateAdapter = true)
data class ScheduleElementModel(
    val date: LocalDate,
    val items: List<ScheduleItemsModel>,
    val actions: List<String>,
) : Mappable<ScheduleResults> {

    override fun map(cachedAt: OffsetDateTime): ScheduleResults {
        var description = ""
        var holiday: String? = null
        val shiftElements = mutableListOf<ShiftElement>()
        val signupElements = mutableListOf<SignupElement>()
        val signupEvents = mutableListOf<SignupEvent>()
        val leaveElements = mutableListOf<LeaveElement>()
        val pendingLeaveElements = mutableListOf<PendingLeaveElement>()
        val projectedShift = mutableListOf<ProjectedShiftElement>()
        val projectedLeave = mutableListOf<ProjectedLeaveElement>()
        val openShiftElements = mutableListOf<OpenShiftElement>()
        val openShiftEvents = mutableListOf<OpenShiftEvent>()
        val tradeEvents = mutableListOf<TradeEvent>()

        items.forEach {
            when (it) {
                is SignupElementModel -> it.map(cachedAt).let { signup ->
                    signupElements.add(signup.signup)
                    signupEvents.addAll(signup.events)
                }
                is HolidayElementModel -> holiday = it.name
                is ShiftElementModel -> shiftElements.add(it.map(cachedAt))
                is LeaveElementModel -> leaveElements.add(it.map(cachedAt))
                is PendingLeaveElementModel -> pendingLeaveElements.add(it.map(cachedAt))
                is ProjectedShiftElementModel -> projectedShift.add(it.map(cachedAt))
                is ProjectedLeaveElementModel -> projectedLeave.add(it.map(cachedAt))
                is OpenShiftElementModel -> it.map(cachedAt).let { openShift ->
                    openShiftElements.add(openShift.openShift)
                    openShiftEvents.addAll(openShift.events)
                }
                is TradeElementModel -> it.map(cachedAt).let { trade ->
                    tradeEvents.add(trade)
                }
                is NoneElementModel -> description = it.description
            }
        }

        return ScheduleResults(
            ScheduleElement(date, description, holiday, actions, cachedAt),
            signupEvents,
            signupElements,
            shiftElements,
            leaveElements,
            pendingLeaveElements,
            projectedShift,
            projectedLeave,
            openShiftElements,
            openShiftEvents,
            tradeEvents
        )
    }
}

data class ScheduleResults(
    val base: ScheduleElement,

    val signupEvents: List<SignupEvent>,
    val signupElements: List<SignupElement>,

    val shiftElements: List<ShiftElement>,
    val leaveElements: List<LeaveElement>,
    val pendingLeaveElements: List<PendingLeaveElement>,
    val projectedShift: List<ProjectedShiftElement>,
    val projectedLeave: List<ProjectedLeaveElement>,

    val openShiftElements: List<OpenShiftElement>,
    val openShiftEvents: List<OpenShiftEvent>,

    val tradeEvents: List<TradeEvent>,
)