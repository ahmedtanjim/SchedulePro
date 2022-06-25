package com.shiftboard.schedulepro.core.persistence.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.shiftboard.schedulepro.core.persistence.model.notification.Notification

@Dao
abstract class NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertNotifications(item: List<Notification>)

    @Query("DELETE FROM Notification")
    abstract suspend fun deleteAllNotifications()

    @Transaction
    @Query("SELECT * FROM Notification ORDER BY datetime(sentDateUtc) desc")
    abstract fun fetchAllNotifications(): PagingSource<Int, Notification>
}