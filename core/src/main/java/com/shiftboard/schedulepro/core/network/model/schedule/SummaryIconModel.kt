package com.shiftboard.schedulepro.core.network.model.schedule

import com.shiftboard.schedulepro.core.network.adapters.AndroidColor
import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class SummaryDate(
    val date: OffsetDateTime,
    val items: List<SummaryIconModel>
)

@JsonClass(generateAdapter = true)
data class SummaryIconModel(
    val type: String,
    @AndroidColor val color: Int,
    val hasOvertime: Boolean,
    val projected: Boolean = false,
)