package com.shiftboard.schedulepro.core.persistence

import androidx.room.*
import com.shiftboard.schedulepro.core.persistence.dao.*
import com.shiftboard.schedulepro.core.persistence.model.PageKey
import com.shiftboard.schedulepro.core.persistence.model.details.*
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification
import com.shiftboard.schedulepro.core.persistence.model.schedule.*

@Database(entities = [
    Notification::class,

    PageKey::class,

    LeaveRequestDetails::class,
    ShiftDetails::class,
    LeaveDetails::class,
    ProjectedShiftDetails::class,
    ProjectedLeaveDetails::class,
    ScheduleElement::class,
    ProjectedShiftElement::class,
    ProjectedLeaveElement::class,
    ShiftElement::class,
    LeaveElement::class,
    PendingLeaveElement::class,
    SignupElement::class,
    SignupEvent::class,
    OpenShiftElement::class,
    OpenShiftEvent::class,
    TradeEvent::class

], version = 25, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun detailsDao(): DetailsDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun notificationDao(): NotificationDao
    abstract fun pageDao(): PageDao
    abstract fun nukeDao(): NukeDao

    companion object {
        const val name = "shiftboard-db"
    }
}

