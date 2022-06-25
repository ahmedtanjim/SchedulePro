package com.shiftboard.schedulepro.core.common.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.shiftboard.schedulepro.core.common.utils.bundleOf

sealed class PageViewAnalyticEvents {
    object Schedule: PageViewEvent("schedule_page", "root_activity")
    object Notifications: PageViewEvent("notifications_page", "root_activity")
    object Profile: PageViewEvent("profile_page", "root_activity")
    object GroupSchedule: PageViewEvent("group_schedule_page", "root_activity")

    object RequestDetail: PageViewEvent("request_detail_page", "root_activity")

    object LeaveDetail: PageViewEvent("leave_detail_page", "root_activity")
    object ProjectedLeaveDetail: PageViewEvent("projected_leave_detail_page", "root_activity")

    object ShiftDetail: PageViewEvent("shift_detail_page", "root_activity")
    object TradeDetail: PageViewEvent("trade_detail_page", "root_activity")
    object ProjectedShiftDetail: PageViewEvent("projected_shift_detail_page", "root_activity")
}

sealed class LeaveRequestAnalyticEvents {
    class OnStart(onSchedulePage: Boolean):
        AnalyticEvent("leave_request_started",
            bundleOf("startedOnSchedule" to onSchedulePage)
        )

    object OnCancel:
        AnalyticEvent("leave_request_canceled")

    object OnFinish:
        AnalyticEvent("leave_request_finished")

    class OnError(error: String):
        AnalyticEvent("leave_request_error",
            bundleOf("error" to error)
        )
}

sealed class SignupAnalyticEvents {
    class OnStart(onSchedulePage: Boolean):
        AnalyticEvent("signup_request_started",
            bundleOf("startedOnSchedule" to onSchedulePage)
        )

    object OnCancel:
        AnalyticEvent("signup_request_dismissed")

    object OnUpdate: AnalyticEvent("signup_request_modified")
    object OnCreate: AnalyticEvent("signup_request_created")

    class OnError(error: String):
        AnalyticEvent("signup_request_error",
            bundleOf("error" to error)
        )
}

sealed class OpenShiftRequestAnalyticEvents {
    class OnStart(onSchedulePage: Boolean):
        AnalyticEvent("open_shift_request_started",
            bundleOf("startedOnSchedule" to onSchedulePage)
        )

    object OnCancel:
        AnalyticEvent("open_shift_request_canceled")

    object OnFinish:
        AnalyticEvent("open_shift_request_finished")

    class OnError(error: String):
        AnalyticEvent("open_shift_request_error",
            bundleOf("error" to error)
        )
}

sealed class TradeRequestAnalyticEvents {
    class OnStart(onSchedulePage: Boolean):
        AnalyticEvent("trade_request_started",
            bundleOf("startedOnSchedule" to onSchedulePage)
        )

    object OnCancel:
        AnalyticEvent("trade_request_canceled")

    object OnApprove:
        AnalyticEvent("trade_request_approved")

    object OnSubmit:
        AnalyticEvent("trade_request_submitted")

    object OnForceSubmit:
        AnalyticEvent("trade_request_force_submitted")

    object OnReject:
        AnalyticEvent("trade_request_rejected")

    object OnFinish:
        AnalyticEvent("trade_request_finished")

    class OnError(error: String):
        AnalyticEvent("trade_request_error",
            bundleOf("error" to error)
        )
}

sealed class UserActionAnalyticEvents {
    object Login: AnalyticEvent(FirebaseAnalytics.Event.LOGIN)
    object Silent_Login: AnalyticEvent("silent_login")
    object Logout: AnalyticEvent("logout")
    object Turndown: AnalyticEvent("turndown")
    object CancelTurndown: AnalyticEvent("cancel_turndown")
}


abstract class AnalyticEvent(val eventName: String, val bundle: Bundle? = null)
abstract class PageViewEvent(val screenName: String, val parentActivity: String)