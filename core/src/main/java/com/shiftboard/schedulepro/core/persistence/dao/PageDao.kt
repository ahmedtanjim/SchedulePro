package com.shiftboard.schedulepro.core.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shiftboard.schedulepro.core.persistence.model.PageKey
import com.shiftboard.schedulepro.core.persistence.model.details.ShiftDetails


// I don't really like this solution, but it is the one that the official docs are using and I
// currently haven't found a better one
@Dao
abstract class PageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(key: PageKey)

    @Query("DELETE FROM PageKey WHERE pageName = :page")
    abstract suspend fun removeKey(page: String)

    @Query("SELECT * FROM PageKey WHERE pageName = :page")
    abstract suspend fun findKey(page: String): PageKey?
}