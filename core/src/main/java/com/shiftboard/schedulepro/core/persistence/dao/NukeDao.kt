package com.shiftboard.schedulepro.core.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
/**
 * TODO :: will probably replace this with a more reasonable method in the future, but this is
 * simple and effective and the is less error prone than trying to manage sessions
 */
abstract class NukeDao {

    @Query("DELETE FROM Notification")
    abstract suspend fun nukeNotification()

    @Query("DELETE FROM PageKey")
    abstract suspend fun nukePageKey()

    @Query("DELETE FROM LeaveRequestDetails")
    abstract suspend fun nukeLeaveRequestDetails()

    @Query("DELETE FROM ShiftDetails")
    abstract suspend fun nukeShiftDetails()

    @Query("DELETE FROM LeaveDetails")
    abstract suspend fun nukeLeaveDetails()

    @Query("DELETE FROM ScheduleElement")
    abstract suspend fun nukeScheduleElement()

    @Query("DELETE FROM ShiftElement")
    abstract suspend fun nukeShiftElement()

    @Query("DELETE FROM LeaveElement")
    abstract suspend fun nukeLeaveElement()

    @Query("DELETE FROM PendingLeaveElement")
    abstract suspend fun nukePendingLeaveElement()

    @Transaction
    open suspend fun nukeDb() {
        nukePendingLeaveElement()
        nukeLeaveElement()
        nukeShiftElement()
        nukeScheduleElement()
        nukeLeaveDetails()
        nukeShiftDetails()
        nukeLeaveRequestDetails()
        nukePageKey()
        nukeNotification()
    }
}