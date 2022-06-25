package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.Entity
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(
    primaryKeys = ["date", "id"]
)
data class SignupElement(
    override val date: LocalDate,
    val id: String,

    val cachedAt: OffsetDateTime,
): ScheduleItem
