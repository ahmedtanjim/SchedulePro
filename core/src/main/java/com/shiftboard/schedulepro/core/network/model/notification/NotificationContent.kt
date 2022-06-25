package com.shiftboard.schedulepro.core.network.model.notification

import com.squareup.moshi.JsonClass
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import org.threeten.bp.LocalDate


fun notificationTypeToClass(type: String): Class<out NotificationContent> {
    PolymorphicJsonAdapterFactory.of(NotificationContent::class.java, "type")
        .withSubtype(LeaveRequestStatusUpdatedContent::class.java, "LeaveRequestStatusUpdated")
        .withSubtype(LeaveRequestCancellationContent::class.java, "LeaveRequestCancellationRequestStatusUpdated")
        .withSubtype(LeaveRequestAmendmentContent::class.java, "LeaveRequestAmendmentRequestStatusUpdated")
        .withSubtype(LeaveRequestDetailsContent::class.java, "LeaveRequestDetailsUpdated")
        .withSubtype(SchedulePublishedContent::class.java, "SchedulePublishedSchedulePublished")
        .withSubtype(SingleOptionalShiftNotification::class.java, "ShiftEdited")
        .withSubtype(SingleOptionalShiftNotification::class.java, "ShiftAssignedOrUnassigned")
        .withSubtype(SingleOptionalShiftNotification::class.java, "SingleOptionalShiftNotification")
        .withSubtype(BulkNotification::class.java, "BulkShiftsEdited")
        .withSubtype(BulkNotification::class.java, "BulkShiftsAssignedOrUnassigned")
        .withSubtype(BulkNotification::class.java, "BulkNotification")
        .withSubtype(BulkMessaging::class.java, "BulkMessaging")
        .withSubtype(OpenShiftNotification::class.java, "OpenShiftsPublished")
        .withSubtype(OpenShiftNotification::class.java, "OpenShiftRequestStatusUpdated")
        .withSubtype(TurndownPosterNoTakersNotification::class.java, "TurndownPosterNoTakers")
        .withSubtype(TurndownPosterReassignedNotification::class.java, "TurndownPosterReassigned")
        .withSubtype(TradeRequested::class.java, "TradeRequested")
        .withSubtype(TradeRequestUpdated::class.java, "TradeRequestUpdated")

    return when (type) {
        "LeaveRequestStatusUpdated" -> LeaveRequestStatusUpdatedContent::class.java
        "LeaveRequestCancellationRequestStatusUpdated" -> LeaveRequestCancellationContent::class.java
        "LeaveRequestAmendmentRequestStatusUpdated" -> LeaveRequestAmendmentContent::class.java
        "LeaveRequestDetailsUpdated" -> LeaveRequestDetailsContent::class.java
        "SchedulePublishedSchedulePublished" -> SchedulePublishedContent::class.java
        "ShiftEdited", "ShiftAssignedOrUnassigned", "SingleOptionalShiftNotification" -> SingleOptionalShiftNotification::class.java
        "BulkShiftsEdited", "BulkShiftsAssignedOrUnassigned", "BulkNotification" -> BulkNotification::class.java
        "BulkMessaging" -> BulkMessaging::class.java
        "OpenShiftsPublished", "OpenShiftRequestStatusUpdated" -> OpenShiftNotification::class.java
        "TurndownPosterReassigned" -> TurndownPosterReassignedNotification::class.java
        "TurndownPosterNoTakers" -> TurndownPosterNoTakersNotification::class.java
        "TradeRequested" -> TradeRequested::class.java
        "TradeRequestUpdated" -> TradeRequestUpdated::class.java
        else -> throw Exception("Invalid type")
    }
}


interface NotificationContent {
    val type: String
}

// LeaveRequestStatusUpdated
@JsonClass(generateAdapter = true)
data class LeaveRequestStatusUpdatedContent(
    override val type: String = "LeaveRequestStatusUpdated",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val leaveRequestId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val leaveRequestStatus: String,
    val leaveTypeDescription: String,
    val managerComments: String = ""
): NotificationContent

//LeaveRequestCancellationRequestStatusUpdated
@JsonClass(generateAdapter = true)
data class LeaveRequestCancellationContent(
    override val type: String = "LeaveRequestCancellationRequestStatusUpdated",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val leaveRequestId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val leaveTypeDescription: String,
    val cancellationRequestApproved: Boolean,
): NotificationContent

//LeaveRequestAmendmentRequestStatusUpdated
@JsonClass(generateAdapter = true)
data class LeaveRequestAmendmentContent(
    override val type: String = "LeaveRequestAmendmentRequestStatusUpdated",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val leaveRequestId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val leaveTypeDescription: String,
    val amendmentRequestApproved: Boolean,
): NotificationContent

//LeaveRequestDetailsUpdated
@JsonClass(generateAdapter = true)
data class LeaveRequestDetailsContent(
    override val type: String = "LeaveRequestDetailsUpdated",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val leaveRequestId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val leaveRequestStatus: String,
    val leaveTypeDescription: String,
    val managerComments: String = "",
    val beforeStartDate: LocalDate,
    val beforeEndDate: LocalDate,
    val beforeLeaveRequestStatus: String,
    val beforeLeaveTypeDescription: String,
    val beforeManagerComments: String = "",
): NotificationContent

// SchedulePublishedSchedulePublished
@JsonClass(generateAdapter = true)
data class SchedulePublishedContent(
    override val type: String = "SchedulePublishedSchedulePublished",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
): NotificationContent

// ShiftEdited
// ShiftAssignedOrUnassigned
@JsonClass(generateAdapter = true)
data class SingleOptionalShiftNotification(
    override val type: String = "SingleOptionalShiftNotification",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val shiftDate: LocalDate,
    val shiftId: String?,
): NotificationContent

// BulkShiftsEdited
// BulkShiftsAssignedOrUnassigned
@JsonClass(generateAdapter = true)
data class BulkNotification(
    override val type: String = "BulkNotification",
    val fromEmployeeId: String,
    val fromEmployeeName: String,
    val shiftDates: List<LocalDate>,
): NotificationContent

// BulkMessaging
@JsonClass(generateAdapter = true)
data class BulkMessaging(
    override val type: String = "BulkMessaging",
    val fromEmployeeId: String = "",
    val fromEmployeeName: String = "",
    val subject: String = "",
    val message: String = ""
): NotificationContent

//Open Shift Notification
@JsonClass(generateAdapter = true)
data class OpenShiftNotification(
    override val type: String = "OpenShiftsPublished",
    val fromEmployeeId: String = "",
    val fromEmployeeName: String = "",
    val positionCode: String = "",
    val locationCode: String = "",
    val status: String = "",
    val startDate: LocalDate = LocalDate.MIN,
    val endDate: LocalDate = LocalDate.MAX
): NotificationContent

@JsonClass(generateAdapter = true)
data class TurndownPosterNoTakersNotification(
    override val type: String = "TurndownPosterNoTakers",
    val fromEmployeeId: String = "",
    val fromEmployeeName: String = "",
    val positionCode: String = "",
    val locationCode: String = "",
    val startTime: LocalDate = LocalDate.MIN,
    val endTime: LocalDate = LocalDate.MAX
): NotificationContent

@JsonClass(generateAdapter = true)
data class TurndownPosterReassignedNotification(
    override val type: String = "TurndownPosterReassigned",
    val fromEmployeeId: String = "",
    val fromEmployeeName: String = "",
    val positionCode: String = "",
    val locationCode: String = "",
    val startTime: LocalDate = LocalDate.MIN,
    val endTime: LocalDate = LocalDate.MAX
): NotificationContent

@JsonClass(generateAdapter = true)
data class TradeRequested(
    override val type: String = "TradeRequested",
    val fromEmployeeId: String = "",
    val fromEmployeeName: String = "",
    val tradeId: String = "",
    val fromDate: LocalDate = LocalDate.MIN,
    val toDate: LocalDate = LocalDate.MAX
): NotificationContent

@JsonClass(generateAdapter = true)
data class TradeRequestUpdated(
    override val type: String = "TradeRequestUpdated",
    val fromEmployeeId: String = "",
    val fromEmployeeName: String = "",
    val tradeId: String = "",
    val fromDate: LocalDate = LocalDate.MIN,
    val toDate: LocalDate = LocalDate.MAX,
    val status: String = "",
): NotificationContent

@JsonClass(generateAdapter = true)
data class InvalidContent(
    override val type: String = "InvalidContent"
): NotificationContent

