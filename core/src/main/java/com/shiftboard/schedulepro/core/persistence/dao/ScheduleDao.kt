package com.shiftboard.schedulepro.core.persistence.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.shiftboard.schedulepro.core.persistence.model.schedule.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate


@Dao
abstract class ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllShifts(elements: List<ShiftElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllLeaves(elements: List<LeaveElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllPendingLeaves(elements: List<PendingLeaveElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertScheduleElement(elements: ScheduleElement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSignupElement(elements: List<SignupElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSignupEvent(elements: List<SignupEvent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertProjectedShiftElement(elements: List<ProjectedShiftElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertProjectedLeaveElement(elements: List<ProjectedLeaveElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOpenShiftElement(elements: List<OpenShiftElement>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOpenShiftEvent(elements: List<OpenShiftEvent>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertTradeEvent(elements: List<TradeEvent>)

    @Query("DELETE FROM ShiftElement")
    abstract suspend fun clearShiftElements()

    @Query("DELETE FROM LeaveElement")
    abstract suspend fun clearLeaveElement()

    @Query("DELETE FROM SignupEvent")
    abstract suspend fun clearSignupEvent()

    @Query("DELETE FROM SignupElement")
    abstract suspend fun clearSignupElement()

    @Query("DELETE FROM ScheduleElement")
    abstract suspend fun clearScheduleElements()

    @Query("DELETE FROM PendingLeaveElement")
    abstract suspend fun clearPendingLeaveElement()

    @Query("DELETE FROM ProjectedShiftElement")
    abstract suspend fun clearProjectedShiftElement()

    @Query("DELETE FROM ProjectedLeaveElement")
    abstract suspend fun clearProjectedLeaveElement()

    @Query("DELETE FROM ShiftElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteShiftElementsInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM LeaveElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteLeaveElementInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM ScheduleElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteScheduleElementsInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM SignupElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteSignupElementsInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM SignupEvent WHERE date(startTime) >= date(:start) AND date(startTime) <= date(:end)")
    abstract suspend fun deleteSignupEventsInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM PendingLeaveElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deletePendingLeaveElementInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM ProjectedShiftElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteProjectedShiftElementInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM ProjectedLeaveElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteProjectedLeaveElementInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM OpenShiftElement WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteOpenShiftElementInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM OpenShiftEvent WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteOpenShiftEventInRange(start: LocalDate, end: LocalDate)

    @Query("DELETE FROM TradeEvent WHERE date(date) >= date(:start) AND date(date) <= date(:end)")
    abstract suspend fun deleteTradeEventInRange(start: LocalDate, end: LocalDate)


    @Transaction
    open suspend fun deleteAllData() {
        clearLeaveElement()
        clearShiftElements()
        clearPendingLeaveElement()
        clearScheduleElements()
        clearSignupEvent()
        clearSignupElement()
        clearProjectedShiftElement()
        clearProjectedLeaveElement()
    }

    @Transaction
    @Query("SELECT * FROM ScheduleElement WHERE date(date) >= date(:start) AND date(date) <= date(:end) ORDER BY date(date)")
    abstract suspend fun findAllShiftsInRange(start: LocalDate, end: LocalDate): List<DayScheduleElement>

    @Transaction
    @Query("SELECT * FROM ScheduleElement ORDER BY date(date)")
    abstract fun findAllShiftsByDay(): PagingSource<Int, DayScheduleElement>

    @Transaction
    @Query("SELECT * FROM ScheduleElement ORDER BY date(date)")
    abstract fun findAllShifts(): Flow<List<DayScheduleElement>>
}
