package com.shiftboard.schedulepro.core.network.model.overtime

import com.shiftboard.schedulepro.core.network.adapters.AndroidColor
import com.shiftboard.schedulepro.resources.OvertimeActions
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@JsonClass(generateAdapter = true)
data class OTSignup(
    val id: String? = null,
    val date: LocalDate,
    val fields: List<SignupField>,
    val shiftTimes: List<SignupShiftTime>,
    val actions: List<String> = listOf(),
    val holidayName: String? = null,
    val lastSubmitted: OffsetDateTime? = null,
) {

    val allowEdits by lazy {
        OvertimeActions.SIGNUP in actions || shiftTimes.any { it.selectable }
    }
}

@JsonClass(generateAdapter = true)
data class SignupField(
    val selectionType: String,
    val required: Boolean,
    val type: String,
    val name: String,
    val selected: List<String> = listOf(),
    val options: List<SignupFieldOptions> = listOf(),
)

@JsonClass(generateAdapter = true)
data class SignupFieldOptions(
    val id: String,
    val name: String,
)

@JsonClass(generateAdapter = true)
data class SignupShiftTime(
    val id: String,
    val date: LocalDate,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val shiftTimeCode: String,
    val shiftTimeDescription: String,
    @AndroidColor val color: Int,
    val cutoffTime: OffsetDateTime,
    val selected: Boolean,
    val selectable: Boolean,
)