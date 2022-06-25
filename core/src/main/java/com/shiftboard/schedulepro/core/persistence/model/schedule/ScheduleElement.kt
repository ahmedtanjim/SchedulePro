package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.*
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


@Entity
data class ScheduleElement(
    @PrimaryKey val date: LocalDate,
    val description: String,
    val holiday: String?,
    val actions: List<String>,
    val cachedAt: OffsetDateTime,
)

interface ScheduleItem {
    val date: LocalDate
}

data class ActionsItem(
    override val date: LocalDate,
    val description: String,
    val actions: List<String>
): ScheduleItem

data class HolidayItem(
    override val date: LocalDate,
    val holiday: String
): ScheduleItem


data class DayScheduleElement(
    @Embedded
    val base: ScheduleElement,

    @Relation(entity = SignupElement::class, parentColumn = "date", entityColumn = "date")
    val signupElements: List<SignupWithEvents>,

    @Relation(parentColumn = "date", entityColumn = "date")
    val shiftElements: List<ShiftElement>,

    @Relation(parentColumn = "date", entityColumn = "date")
    val leaveElements: List<LeaveElement>,

    @Relation(parentColumn = "date", entityColumn = "date")
    val pendingLeaveElements: List<PendingLeaveElement>,

    @Relation(parentColumn = "date", entityColumn = "date")
    val projectedShiftElements: List<ProjectedShiftElement>,

    @Relation(parentColumn = "date", entityColumn = "date")
    val projectedLeaveElements: List<ProjectedLeaveElement>,

    @Relation(entity = OpenShiftElement::class, parentColumn = "date", entityColumn = "date")
    val openShiftElements: List<OpenShiftWithEvents>,

    @Relation(parentColumn = "date", entityColumn = "date")
    val tradeEvents: List<TradeEvent>
) {
    fun elementCount(): Int {
        return signupElements.size + shiftElements.size + leaveElements.size + pendingLeaveElements.size + projectedShiftElements.size + projectedLeaveElements.size + openShiftElements.size + tradeEvents.size
    }
}

enum class ScheduleElementType {
    LEAVE, LEAVE_REQUEST, SHIFT, NONE, ACTIONS, SIGN_UPS, HOLIDAY, PROJECTED_SHIFT, PROJECTED_LEAVE, OPEN_SHIFT, OPEN_SHIFT_REQUEST, TRADE_REQUEST;
}