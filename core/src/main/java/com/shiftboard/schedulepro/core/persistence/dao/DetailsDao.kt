package com.shiftboard.schedulepro.core.persistence.dao

import androidx.room.*
import com.shiftboard.schedulepro.core.persistence.model.details.*
import org.threeten.bp.LocalDate

@Dao
abstract class DetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(shiftDetails: ShiftDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(details: LeaveDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(details: LeaveRequestDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(details: ProjectedLeaveDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(details: ProjectedShiftDetails)

    @Query("DELETE FROM ShiftDetails WHERE id == :id")
    abstract suspend fun deleteShiftDetailsById(id: String)

    @Query("DELETE FROM LeaveDetails WHERE id == :id")
    abstract suspend fun deleteLeaveDetailsById(id: String)

    @Query("DELETE FROM LeaveRequestDetails WHERE id == :id")
    abstract suspend fun deleteLeaveRequestDetailsById(id: String)

    @Query("DELETE FROM ProjectedLeaveDetails WHERE date == :date AND leaveTypeId == :id")
    abstract suspend fun deleteProjectedLeaveRequestDetailsById(date: LocalDate, id: String)

    @Query("DELETE FROM ProjectedShiftDetails WHERE date == :date AND shiftTimeId == :id")
    abstract suspend fun deleteProjectedShiftRequestById(date: LocalDate, id: String)

    @Query("SELECT * FROM ShiftDetails WHERE id == :id")
    abstract suspend fun findShiftDetailsById(id: String): ShiftDetails?

    @Transaction
    @Query("SELECT * FROM LeaveDetails WHERE id == :id")
    abstract suspend fun findLeaveDetailsById(id: String): Leave?

    @Query("SELECT * FROM LeaveRequestDetails WHERE id == :id")
    abstract suspend fun findLeaveRequestDetailsById(id: String): LeaveRequestDetails?

    @Query("SELECT * FROM ProjectedLeaveDetails WHERE date == :date AND leaveTypeId == :id")
    abstract suspend fun findProjectedLeaveRequestDetails(date: LocalDate, id: String): ProjectedLeaveDetails?

    @Query("SELECT * FROM ProjectedShiftDetails WHERE date == :date AND shiftTimeId == :id")
    abstract suspend fun findProjectedShiftRequestDetails(date: LocalDate, id: String): ProjectedShiftDetails?
}