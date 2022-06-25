package com.shiftboard.schedulepro.resources

object ScheduleListActions {
    const val SUBMIT_LEAVE = "SubmitLeaveRequest"
    const val SUBMIT_SIGN_UP = "SubmitSignUp"
    const val SUBMIT_BID = "SubmitOpenShiftRequest"
}

object PendingLeaveStatus {
    const val PENDING = "Pending"
    const val ACCEPTED = "Accepted"
    const val PENDING_EDIT = "Accepted_PendingEdit"
    const val ACCEPTED_CR = "Accepted_CancellationRequested"
    const val DECLINED = "Declined"
    const val REVOKED = "RevokedAfterApproval"
    const val CANCELLED = "CancelledAfterApproval"

    fun isAccepted(state: String): Boolean {
        return when (state) {
            ACCEPTED, PENDING_EDIT, ACCEPTED_CR -> true
            else -> false
        }
    }
}

object SignupSelectionType {
    const val SINGLE = "Single"
    const val MULTI = "Multiple"
}

object OvertimeActions {
    const val SIGNUP = "Submit"
}