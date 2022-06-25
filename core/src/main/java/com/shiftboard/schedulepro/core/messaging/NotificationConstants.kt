package com.shiftboard.schedulepro.core.messaging

import com.shiftboard.schedulepro.core.R
import com.shiftboard.schedulepro.core.network.model.notification.*
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import org.threeten.bp.LocalDate

object NotificationConstants {
    const val EXTRA_START_DATE = "startDate"
    const val EXTRA_ID = "notificationId"
    const val EXTRA_BULK_MESSAGE = "bulk_messaging"

    const val LEAVE_UPDATES_CHANNEL = "leave_updates"
    const val SCHEDULE_UPDATES_CHANNEL = "schedule_updates"
    const val SHIFT_UPDATES_CHANNEL = "shift_updates"
    const val BULK_MESSAGE_CHANNEL = "bulk_message"
    const val TRADE_CHANNEL = "trades"

    const val LEAVE_UPDATE = "LeaveRequestStatusUpdated"
    const val LEAVE_CANCEL_UPDATE = "LeaveRequestCancellationRequestStatusUpdated"
    const val LEAVE_AMENDMENT_UPDATE = "LeaveRequestAmendmentRequestStatusUpdated"
    const val LEAVE_DETAIL_UPDATE = "LeaveRequestDetailsUpdated"

    const val SCHEDULE_PUBLISHED = "SchedulePublished"

    const val SHIFT_EDIT = "ShiftEdited"
    const val SHIFT_ASSIGN = "ShiftAssignedOrUnassigned"

    const val BULK_SHIFT_EDIT = "BulkShiftsEdited"
    const val BULK_SHIFT_ASSIGN = "BulkShiftsAssignedOrUnassigned"

    const val BULK_MESSAGE = "BulkMessaging"

    const val OPEN_SHIFT_PUBLISHED = "OpenShiftsPublished"
    const val OPEN_SHIFT_UPDATED = "OpenShiftRequestUpdated"
    const val OPEN_SHIFT_STATUS_UPDATED = "OpenShiftRequestStatusUpdated"

    const val TURNDOWN_REASSIGNED = "TurndownPosterReassigned"
    const val TURNDOWN_NO_TAKERS = "TurndownPosterNoTakers"

    const val TRADE_REQUESTED = "TradeRequested"
    const val TRADE_REQUEST_UPDATED = "TradeRequestUpdated"

    val allTypes = listOf(
        LEAVE_UPDATE,
        LEAVE_CANCEL_UPDATE,
        LEAVE_AMENDMENT_UPDATE,
        LEAVE_DETAIL_UPDATE,
        SCHEDULE_PUBLISHED,
        SHIFT_EDIT,
        SHIFT_ASSIGN,
        BULK_SHIFT_EDIT,
        BULK_SHIFT_ASSIGN,
        BULK_MESSAGE,
        OPEN_SHIFT_PUBLISHED,
        OPEN_SHIFT_UPDATED,
        OPEN_SHIFT_STATUS_UPDATED,
        TURNDOWN_REASSIGNED,
        TURNDOWN_NO_TAKERS,
        TRADE_REQUESTED,
        TRADE_REQUEST_UPDATED
    )

    fun mapTypeToChannel(type: String): String {
        return when (type) {
            LEAVE_UPDATE, LEAVE_CANCEL_UPDATE, LEAVE_AMENDMENT_UPDATE, LEAVE_DETAIL_UPDATE,
            -> LEAVE_UPDATES_CHANNEL

            SCHEDULE_PUBLISHED,
            -> SCHEDULE_UPDATES_CHANNEL

            SHIFT_EDIT,
            SHIFT_ASSIGN,
            BULK_SHIFT_EDIT,
            BULK_SHIFT_ASSIGN -> SHIFT_UPDATES_CHANNEL

            BULK_MESSAGE -> BULK_MESSAGE_CHANNEL

            OPEN_SHIFT_PUBLISHED,
            OPEN_SHIFT_UPDATED,
            TURNDOWN_NO_TAKERS,
            TURNDOWN_REASSIGNED,
            OPEN_SHIFT_STATUS_UPDATED -> SHIFT_UPDATES_CHANNEL

            TRADE_REQUESTED,
                TRADE_REQUEST_UPDATED -> TRADE_CHANNEL
            else
            -> throw Error("Invalid Message Type")
        }
    }

    fun mapTypeToTitleRes(type: String): Int {
        return when (type) {
            LEAVE_UPDATE, LEAVE_CANCEL_UPDATE, LEAVE_AMENDMENT_UPDATE, LEAVE_DETAIL_UPDATE,
            -> R.string.leave_request_notification_title

            SCHEDULE_PUBLISHED, SHIFT_EDIT, SHIFT_ASSIGN, BULK_SHIFT_EDIT, BULK_SHIFT_ASSIGN,
            -> R.string.schedule_notification_title

            OPEN_SHIFT_UPDATED, OPEN_SHIFT_PUBLISHED, OPEN_SHIFT_STATUS_UPDATED -> R.string.bids

            TURNDOWN_NO_TAKERS, TURNDOWN_REASSIGNED -> R.string.turndown

            TRADE_REQUESTED, TRADE_REQUEST_UPDATED -> R.string.trades
            else
            -> throw Error("Invalid Message Type")
        }
    }
}

// Making this an extension function because we may have additional routes at some points.
fun Notification.focusDate(): LocalDate? {
    return when (content) {
        is LeaveRequestStatusUpdatedContent -> content.startDate
        is LeaveRequestCancellationContent -> content.startDate
        is LeaveRequestAmendmentContent -> content.startDate
        is LeaveRequestDetailsContent -> content.startDate
        is SchedulePublishedContent -> content.startDate
        is SingleOptionalShiftNotification -> content.shiftDate
        is BulkNotification -> content.shiftDates.firstOrNull()
        is OpenShiftNotification -> content.startDate
        is TurndownPosterReassignedNotification -> content.startTime
        is TurndownPosterNoTakersNotification -> content.endTime
        else -> null
    }
}

fun Notification.focusEndDate(): LocalDate? {
    return when (content) {
        is LeaveRequestStatusUpdatedContent -> content.endDate
        is LeaveRequestCancellationContent -> content.endDate
        is LeaveRequestAmendmentContent -> content.endDate
        is LeaveRequestDetailsContent -> content.endDate
        is SchedulePublishedContent -> content.endDate
        is SingleOptionalShiftNotification -> content.shiftDate
        is BulkNotification -> content.shiftDates.lastOrNull()
        is OpenShiftNotification -> content.endDate
        is TurndownPosterReassignedNotification -> content.startTime
        is TurndownPosterNoTakersNotification -> content.endTime
        else -> null
    }
}