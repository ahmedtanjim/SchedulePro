package com.shiftboard.schedulepro.core.network.api

import com.serjltt.moshi.adapters.Wrapped
import com.shiftboard.schedulepro.core.network.common.ApiVersion
import com.shiftboard.schedulepro.core.network.model.*
import com.shiftboard.schedulepro.core.network.model.details.*
import com.shiftboard.schedulepro.core.network.model.group.GroupFiltersResponse
import com.shiftboard.schedulepro.core.network.model.group.GroupSchedulesResponse
import com.shiftboard.schedulepro.core.network.model.notification.NotificationModel
import com.shiftboard.schedulepro.core.network.model.notification.RegistrationRequest
import com.shiftboard.schedulepro.core.network.model.openshift.OpenShiftDetails
import com.shiftboard.schedulepro.core.network.model.openshift.OpenShiftDetailsCollection
import com.shiftboard.schedulepro.core.network.model.openshift.PostOpenShiftResponse
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignup
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignupPost
import com.shiftboard.schedulepro.core.network.model.profile.*
import com.shiftboard.schedulepro.core.network.model.schedule.ScheduleElementModel
import com.shiftboard.schedulepro.core.network.model.schedule.SummaryDate
import com.shiftboard.schedulepro.core.network.model.trade.TradeRequest
import com.shiftboard.schedulepro.core.network.model.trade.TradeValidationResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ScheduleProApi {

    @ApiVersion("1.0")
    @GET("organization")
    suspend fun fetchOrganization(): Response<Organization>

    @ApiVersion("1.0")
    @GET("my/notifications/new")
    suspend fun fetchNotificationCount(): Response<Int>

    @ApiVersion("1.0")
    @GET("my/profile")
    suspend fun me(): Response<UserData>

    @ApiVersion("1.0")
    @GET("my/permissions")
    suspend fun permissions(): Response<ApiPermissions>

    @ApiVersion("1.0")
    @Wrapped(path = ["leaveTypes"])
    @GET("leaveTypes")
    suspend fun listLeaveTypes(): Response<List<LeaveElementModel>>

    @ApiVersion("1.0")
    @Wrapped(path = ["scheduleItemCollections"])
    @GET("my/schedule")
    suspend fun fetchSchedule(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<List<ScheduleElementModel>>

    @ApiVersion("1.0")
    @Wrapped(path = ["scheduleSummary"])
    @GET("my/schedule/summary")
    suspend fun fetchScheduleSummary(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<List<SummaryDate>>

    @ApiVersion("1.0")
    @GET("my/shifts/{id}")
    suspend fun fetchShiftDetails(
        @Path("id") id: String,
    ): Response<ShiftDetailsModel>

    @ApiVersion("1.0")
    @GET("/my/shifts/projected/{date}/{id}")
    suspend fun fetchProjectedShiftDetails(
        @Path("date") date: String,
        @Path("id") id: String,
    ): Response<ProjectedShiftModel>

    @ApiVersion("1.0")
    @POST("my/leaves/requests")
    suspend fun postLeaveRequest(
        @Body leaveRequest: LeaveRequest,
    ): Response<ResponseBody>

    @ApiVersion("1.0")
    @POST("notifications/devices/register")
    suspend fun postDeviceRegistration(
        @Body deviceRegistration: RegistrationRequest,
    ): Response<ResponseBody>

    @ApiVersion("1.0")
    @GET("my/leaves/{id}")
    suspend fun fetchLeaveDetails(
        @Path("id") id: String,
    ): Response<LeaveDetailsModel>

    @ApiVersion("1.0")
    @GET("my/leaves/requests/{id}")
    suspend fun fetchLeaveRequestDetails(
        @Path("id") id: String,
    ): Response<LeaveRequestDetailModel>

    @ApiVersion("1.0")
    @GET("/my/leaves/projected/{date}/{id}")
    suspend fun fetchProjectedLeaveDetails(
        @Path("date") date: String,
        @Path("id") id: String,
    ): Response<ProjectedLeaveModel>

    @ApiVersion("1.0")
    @PATCH("my/leaves/requests/{id}")
    suspend fun leaveRequestDelete(
        @Path("id") id: String,
        @Body action: LeaveRequestAction,
    ): Response<ResponseBody>

    @ApiVersion("1.0")
    @PATCH("my/leaves/requests/{id}")
    suspend fun leaveRequestCancel(
        @Path("id") id: String,
        @Body action: LeaveRequestAction,
    ): Response<LeaveRequestDetailModel>

    @ApiVersion("1.0")
    @GET("my/notifications")
    suspend fun fetchNotificationPage(
        @Query("page") page: Int?,
        @Query("size") size: Int? = null,
    ): Response<Page<NotificationModel>>

    @ApiVersion("1.0")
    @GET("my/notifications/{id}")
    suspend fun fetchNotificationDetails(
        @Path("id") id: String,
    ): Response<NotificationModel>

    @ApiVersion("1.0")
    @PATCH("my/notifications/{id}")
    suspend fun markNotificationRead(
        @Path("id") id: String,
    ): Response<ResponseBody>

    @ApiVersion("1.0")
    @GET("my/signups/{date}")
    suspend fun fetchSignupForDate(
        @Path("date") date: String,
    ): Response<OTSignup>

    @ApiVersion("1.0")
    @POST("my/signups/{date}")
    suspend fun postSignupUpdate(
        @Path("date") date: String,
        @Body data: OTSignupPost,
    ): Response<String>

    @ApiVersion("1.0")
    @GET("my/shifts/open")
    suspend fun fetchOpenShifts(
        @Query("startDate") startDate: String,
    ): Response<OpenShiftDetailsCollection>

    @ApiVersion("1.0")
    @POST("my/shifts/open/{requestID}")
    suspend fun postOpenShiftRequestUpdate(
        @Path("requestID") requestID: String,
    ): Response<PostOpenShiftResponse>

    @ApiVersion("1.0")
    @DELETE("my/shifts/open/requests/{requestID}")
    suspend fun deleteOpenShiftRequest(
        @Path("requestID") requestID: String,
    ): Response<Boolean>

    @ApiVersion("1.0")
    @POST("my/shifts/{shiftID}/turndown")
    suspend fun postTurndownShift(
        @Path("shiftID") shiftID: String,
    ): Response<OpenShiftDetails>

    @ApiVersion("1.0")
    @DELETE("my/shifts/{shiftID}/turndown")
    suspend fun deleteTurndownShift(
        @Path("shiftID") shiftID: String,
    ): Response<Boolean>

    @ApiVersion("1.0")
    @POST("user/authenticated")
    suspend fun postLogInEvent(): Response<Boolean>

    @ApiVersion("1.0")
    @GET("trades/{employeeId}/recipients")
    suspend fun getTradableEmplyoees(
        @Path("employeeId") employeeId: String
    ): Response<List<Employee>>

    @ApiVersion("1.0")
    @GET("trades/shifts/{employeeId}")
    suspend fun getTradableShiftsForEmployee(
        @Path("employeeId") employeeId: String,
        @Query("shiftDate") shiftDate: String
    ): Response<TradeShiftCollection>


    @ApiVersion("1.0")
    @GET(" trades/{tradeId}")
    suspend fun getTradeDetails(
        @Path("tradeId") tradeId: String
    ): Response<TradeDetailsElement>


    @ApiVersion("1.0")
    @PUT("trades/accept/{tradeId}")
    suspend fun acceptTrade(
        @Path("tradeId") tradeId: String
    ): Response<Boolean>

    @ApiVersion("1.0")
    @PUT("trades/decline/{tradeId}")
    suspend fun declineTrade(
        @Path("tradeId") tradeId: String
    ): Response<Boolean>

    @ApiVersion("1.0")
    @PUT("trades/cancel/{tradeId}")
    suspend fun cancelTrade(
        @Path("tradeId") tradeId: String
    ): Response<Boolean>

    @ApiVersion("1.0")
    @POST("trades/request")
    suspend fun submitTrade(
        @Body tradeRequest: TradeRequest
    ): Response<String>

    @ApiVersion("1.0")
    @POST("trades/validate")
    suspend fun validateTrade(
        @Body tradeRequest: TradeRequest
    ): Response<TradeValidationResponse>

    @ApiVersion("1.0")
    @GET("groups/filters")
    suspend fun getGroupFilters(
    ): Response<GroupFiltersResponse>

    @ApiVersion("1.0")
    @POST("groups/schedule")
    suspend fun getGroupSchedule(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Body filters: GroupFiltersResponse
    ): Response<GroupSchedulesResponse>

    @ApiVersion("1.0")
    @GET("unavailabilities/{employeeId}")
    suspend fun getAvailabilities(
        @Path("employeeId") employeeId: String,
    ): Response<UnavailabilityResponse>

    @ApiVersion("1.0")
    @GET("unavailabilities/recurrences/{employeeId}")
    suspend fun getRecurrences(
        @Path("employeeId") employeeId: String,
    ): Response<RecurrencesResponse>

    @POST("unavailabilities")
    suspend fun addUnavailabilities(
        @Body unavailability: Unavailability
    ): Response<String>

    @POST("unavailabilities/recurrence")
    suspend fun addRecurrences(
        @Body recurrence: Recurrence
    ): Response<String>

    @ApiVersion("1.0")
    @DELETE("unavailabilities/delete/{unavailabilityId}")
    suspend fun deleteUnavailability(
        @Path("unavailabilityId") unavailabilityId: String
    ): Response<Unit>

    @ApiVersion("1.0")
    @DELETE("unavailabilities/recurrences/delete/{unavailabilityRecurrenceId}")
    suspend fun deleteRecurrence(
        @Path("unavailabilityRecurrenceId") unavailabilityRecurrenceId: String
    ): Response<Unit>
}