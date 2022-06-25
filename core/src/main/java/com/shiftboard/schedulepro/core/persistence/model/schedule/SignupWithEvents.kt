package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime


data class SignupWithEvents(
    @Embedded val signup: SignupElement,
    @Relation(
        parentColumn = "id",
        entityColumn = "signupId"
    )
    val events: List<SignupEvent>
): ScheduleItem {
    override val date: LocalDate
        get() = signup.date
}

@Entity(
    primaryKeys = ["signupId", "shiftTimeCode"])
data class SignupEvent(
    val signupId: String,

    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,

    val shiftTimeCode: String,
    val color: Int,
)