package com.shiftboard.schedulepro.core.persistence.model.schedule

import androidx.room.Entity
import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.network.model.details.ShiftDetailsModel
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@Entity(
    primaryKeys = ["id", "date"])
data class TradeEvent(
    val id: String,
    override val date: LocalDate,
    val status: Int?,
    val originatorName: String?,
    val recipientName: String?,
    val isOriginator: Boolean?,
    val cachedAt: OffsetDateTime?
) : ScheduleItem