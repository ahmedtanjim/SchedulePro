package com.shiftboard.schedulepro.core.messaging

import android.content.Context
import com.shiftboard.schedulepro.core.R
import com.shiftboard.schedulepro.core.common.utils.displayDate
import com.shiftboard.schedulepro.core.common.utils.isSameDay
import com.shiftboard.schedulepro.core.network.model.notification.*
import org.threeten.bp.LocalDate
import java.util.*

object MessagingTemplates {

    // Message type will be parsed here
    // data will be a hash map of the raw string values returned in the blob from the notification call
    fun parseMessage(context: Context, type: String, data: NotificationContent): String {
        // when is an advanced switch map if you add a new message type make sure you add it here as well
        return when (type) {
            "LeaveRequestStatusUpdated" -> parseLeaveRequestStatusUpdated(context,
                data as LeaveRequestStatusUpdatedContent)
            "LeaveRequestCancellationRequestStatusUpdated" -> parseLeaveRequestCancellationRequestStatusUpdated(
                context,
                data as LeaveRequestCancellationContent)
            "LeaveRequestAmendmentRequestStatusUpdated" -> parseLeaveRequestAmendmentRequestStatusUpdated(
                context,
                data as LeaveRequestAmendmentContent)
            "LeaveRequestDetailsUpdated" -> parseLeaveRequestDetailsUpdated(context,
                data as LeaveRequestDetailsContent)
            "SchedulePublished" -> parseSchedulePublished(context, data as SchedulePublishedContent)

            "ShiftEdited" -> parseShiftEdited(context, data as SingleOptionalShiftNotification)
            "ShiftAssigned" -> parseShiftAssigned(context, data as SingleOptionalShiftNotification)
            "ShiftUnassigned" -> parseShiftUnassigned(context, data as SingleOptionalShiftNotification)

            "ShiftAssignedOrUnassigned" -> parseShiftAssignedOrUnassignedNoShiftId(context,
                data as SingleOptionalShiftNotification)
            "BulkShiftsEdited" -> parseBulkShiftsEdited(context, data as BulkNotification)
            "BulkShiftsAssignedOrUnassigned" -> parseBulkShiftsAssignedOrUnassigned(context,
                data as BulkNotification)
            "BulkMessaging" -> parseBulkMessagingContent(context, data as BulkMessaging)
            "OpenShiftsPublished" -> parseOpenShiftPublished(context, data as OpenShiftNotification)
            "OpenShiftRequestStatusUpdated" -> parseOpenShiftStatusUpdated(context, data as OpenShiftNotification)
            "TurndownPosterNoTakers" -> parseTurndownNoTakers(context, data as TurndownPosterNoTakersNotification)
            "TurndownPosterReassigned" -> parseTurndownReassigned(context, data as TurndownPosterReassignedNotification)
            "TradeRequested" -> parseTradeRequested(context, data as TradeRequested)
            "TradeRequestUpdated" -> parseTradeRequestUpdated(context, data as TradeRequestUpdated)
            else ->  {
                throw Error("Invalid Message Type")
            }
        }
    }

    fun notificationTitle(context: Context,
                          type: String,
                          data: NotificationContent?): String {
        return when(type) {
            "BulkMessaging" -> (data as BulkMessaging).subject
            else -> return context.getString(NotificationConstants.mapTypeToTitleRes(type))
        }
    }

    private fun parseLeaveRequestStatusUpdated(
        context: Context,
        data: LeaveRequestStatusUpdatedContent,
    ): String {
        return context.getString(R.string.leave_request_status_updated,
            data.leaveTypeDescription,
            dateRangeTransform(context,
                data.startDate,
                data.endDate),
            leaveStatusTransformer(context, data.leaveRequestStatus),
            data.fromEmployeeName
        )
    }

    private fun parseLeaveRequestCancellationRequestStatusUpdated(
        context: Context,
        data: LeaveRequestCancellationContent,
    ): String {
        return context.getString(R.string.leave_request_cancellation_request_status_updated,
            data.leaveTypeDescription,
            dateRangeTransform(context,
                data.startDate,
                data.endDate),
            leaveCanceledTransformer(context, data.cancellationRequestApproved),
            data.fromEmployeeName
        )
    }

    private fun parseLeaveRequestAmendmentRequestStatusUpdated(
        context: Context,
        data: LeaveRequestAmendmentContent,
    ): String {
        return context.getString(R.string.leave_request_amendment_request_status_updated,
            data.leaveTypeDescription,
            dateRangeTransform(context,
                data.startDate,
                data.endDate),
            leaveAmendmentTransformer(context, data.amendmentRequestApproved),
            data.fromEmployeeName
        )
    }

    private fun parseLeaveRequestDetailsUpdated(
        context: Context,
        data: LeaveRequestDetailsContent,
    ): String {
        return context.getString(R.string.leave_request_details_updated,
            data.beforeLeaveTypeDescription,
            dateRangeTransform(context,
                data.beforeStartDate,
                data.beforeEndDate),
            data.fromEmployeeName
        )
    }

    private fun parseSchedulePublished(context: Context, data: SchedulePublishedContent): String {
        return context.getString(R.string.schedule_published,
            dateRangeTransform(context,
                data.startDate,
                data.endDate)
        )
    }

    private fun parseShiftEdited(context: Context, data: SingleOptionalShiftNotification): String {
        return context.getString(R.string.shift_edited,
            data.shiftDate.displayDate(),
            data.fromEmployeeName
        )
    }

    private fun parseShiftAssignedOrUnassignedNoShiftId(
        context: Context,
        data: SingleOptionalShiftNotification,
    ): String {
        return context.getString(R.string.shift_assigned_or_unassigned_no_shift_id,
            data.shiftDate.displayDate(),
            data.fromEmployeeName
        )
    }

    private fun parseShiftAssigned(
        context: Context,
        data: SingleOptionalShiftNotification,
    ): String {
        return context.getString(R.string.shift_assigned,
            data.shiftId,
            data.shiftDate.displayDate(),
            data.fromEmployeeName
        )
    }

    private fun parseShiftUnassigned(
        context: Context,
        data: SingleOptionalShiftNotification,
    ): String {
        return context.getString(R.string.shift_unassigned,
            data.shiftDate.displayDate(),
            data.fromEmployeeName
        )
    }

    private fun parseBulkShiftsEdited(context: Context, data: BulkNotification): String {
        return context.getString(
            R.string.bulk_shifts_edited,
            data.fromEmployeeName,
            dateListTransformer(context, data.shiftDates)
        )
    }

    private fun parseBulkShiftsAssignedOrUnassigned(
        context: Context,
        data: BulkNotification,
    ): String {
        return context.getString(
            R.string.bulk_shifts_assigned_or_unassigned,
            data.fromEmployeeName,
            dateListTransformer(context, data.shiftDates)
        )
    }

    private fun parseBulkMessagingContent(
        context: Context,
        data: BulkMessaging,
    ): String {
        return data.message
    }

    private fun parseOpenShiftPublished(
        context: Context,
        data: OpenShiftNotification,
    ): String {
        return context.getString(R.string.open_shift_published,
            data.fromEmployeeName,
            data.startDate.displayDate()
        )
    }

    private fun parseOpenShiftStatusUpdated(
        context: Context,
        data: OpenShiftNotification,
    ): String {
        return context.getString(R.string.open_shift_request_status_updated,
            data.positionCode,
            data.locationCode,
            data.status,
            data.fromEmployeeName
        )
    }
    private fun leaveCanceledTransformer(context: Context, status: Boolean): String {
        return if (status) context.getString(R.string.notification_leave_canceled_true)
        else context.getString(R.string.notification_leave_canceled_false)
    }

    private fun leaveAmendmentTransformer(context: Context, status: Boolean): String {
        return if (status) context.getString(R.string.notification_leave_amendment_true)
        else context.getString(R.string.notification_leave_amendment_false)
    }

    private fun parseTurndownNoTakers(
        context: Context,
        data: TurndownPosterNoTakersNotification,
    ): String {
        return context.getString(R.string.turndown_no_takers_body,
            data.positionCode,
            data.locationCode,
            data.startTime
        )
    }
    private fun parseTurndownReassigned(
        context: Context,
        data: TurndownPosterReassignedNotification,
    ): String {
        return context.getString(R.string.turndown_reassigned_body,
            data.positionCode,
            data.locationCode,
            data.startTime
        )
    }
    private fun parseTradeRequested(
        context: Context,
        data: TradeRequested,
    ): String {
        return context.getString(R.string.trade_requested_body,
            data.fromEmployeeName,
            data.fromDate,
            data.toDate
        )
    }
    private fun parseTradeRequestUpdated(
        context: Context,
        data: TradeRequestUpdated,
    ): String {
        return context.getString(R.string.trade_request_updated_body,
            data.fromDate,
            data.fromEmployeeName,
            data.status
        )
    }
    private fun dateListTransformer(context: Context, dates: List<LocalDate>): String {
        val sb: StringBuilder = StringBuilder()
        when (dates.size) {
            0 -> sb.append("")
            1 -> sb.append(dates[0].displayDate())
            2 -> sb.append("${dates[0].displayDate()} ${context.getString(R.string.notification_date_list_join_final)} ${dates[1].displayDate()}")
            else -> {
                // sublist of all but 1
                sb.append(dates.subList(0, dates.size - 1)
                    .joinToString(separator = "${context.getString(R.string.notification_date_list_join)} ") { it.displayDate() })
                sb.append(" ${context.getString(R.string.notification_date_list_join_final)} ${dates[dates.size - 1].displayDate()}")
            }
        }
        return sb.toString()
    }

    private fun leaveStatusTransformer(context: Context, status: String): String {
        // English locale is fine because the status is always english
        return when (status.toLowerCase(Locale.ENGLISH)) {
            "pending" -> context.getString(R.string.leave_status_map_pending)
            "accepted" -> context.getString(R.string.leave_status_map_accepted)
            "accepted_pendingedit" -> context.getString(R.string.leave_status_map_accepted_pending_edit)
            "accepted_cancellationrequested" -> context.getString(R.string.leave_status_map_accepted_cancellation_requested)
            "declined" -> context.getString(R.string.leave_status_map_declined)
            "revokedafterapproval" -> context.getString(R.string.leave_status_map_revoked_after_approval)
            "cancelledafterapproval" -> context.getString(R.string.leave_status_map_cancelled_after_approval)
            // Just in case someone adds a new status.  If we didn't have this the app would crash.
            else -> ""
        }
    }

    private fun dateRangeTransform(context: Context, start: LocalDate, end: LocalDate): String {
        return if (start.isSameDay(end)) {
            start.displayDate()
        } else {
            "${start.displayDate()} ${context.getString(R.string.notification_date_range_join)} ${end.displayDate()}"
        }
    }
}