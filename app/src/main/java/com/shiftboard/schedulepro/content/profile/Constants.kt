package com.shiftboard.schedulepro.content.profile

import com.shiftboard.schedulepro.core.network.model.profile.DaysOfWeek
import com.shiftboard.schedulepro.core.network.model.profile.PatternType
import com.shiftboard.schedulepro.core.network.model.profile.TimeSlot
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month

object Constants {
    val timeSlotList = mutableListOf(
        TimeSlot(
            subject = "Family Vacation",
            repeatType = PatternType.Weekly,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(16, 0),
            endDate = LocalDate.of(2023, Month.JANUARY, 1),
            daysOn = DaysOfWeek(
                sunday = true,
                monday = false,
                tuesDay = true,
                wednesday = true,
                thursday = true,
                friday = true,
                saturday = true
            ),
            date = null,
            repeatPeriod = 1
        ),

        TimeSlot(
            subject = "Private Holiday",
            repeatType = PatternType.Weekly,
            startTime = LocalTime.of(0, 0),
            endTime = LocalTime.of(23, 59),
            endDate = LocalDate.of(2023, Month.JUNE, 3),
            daysOn = DaysOfWeek(
                sunday = false,
                monday = false,
                tuesDay = true,
                wednesday = true,
                thursday = true,
                friday = false,
                saturday = false
            ),
            date = null,
            repeatPeriod = 2
        ),

        TimeSlot(
            subject = "Unnamed Time Off",
            repeatType = PatternType.Yearly,
            startTime = LocalTime.of(0, 0),
            endTime = LocalTime.of(23, 59),
            endDate = LocalDate.of(2023, Month.JUNE, 2),
            daysOn = DaysOfWeek(
                sunday = false,
                monday = false,
                tuesDay = true,
                wednesday = true,
                thursday = true,
                friday = false,
                saturday = false
            ),
            date = LocalDate.of(0, Month.JANUARY, 7),
            repeatPeriod = 1
        ),

        TimeSlot(
            subject = "Lorem ipsum",
            repeatType = PatternType.Weekly,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(21, 0),
            endDate = LocalDate.of(2023, Month.JANUARY, 4),
            daysOn = DaysOfWeek(
                sunday = true,
                monday = true,
                tuesDay = true,
                wednesday = false,
                thursday = true,
                friday = false,
                saturday = true
            ),
            date = null,
            repeatPeriod = 3
        ),

        TimeSlot(
            subject = "Unnamed",
            repeatType = PatternType.Monthly,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(21, 0),
            endDate = LocalDate.of(2023, Month.JUNE, 1),
            daysOn = DaysOfWeek(
                sunday = false,
                monday = false,
                tuesDay = true,
                wednesday = true,
                thursday = true,
                friday = false,
                saturday = false
            ),
            date = LocalDate.of(0, 1, 1),
            repeatPeriod = 2
        )
    )
}