package com.shiftboard.schedulepro.core.network.model.overtime

import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDate


@JsonClass(generateAdapter = true)
data class OTSignupPost(
    val id: String?,
    val date: LocalDate,
    val fieldSelections: List<FieldSelection>,
    val shiftTimeIds: List<String>
)

@JsonClass(generateAdapter = true)
data class FieldSelection(
    val type: String,
    val selectedIds: List<String>
)

sealed class OTSignupResult {
    data class Success(val date: LocalDate): OTSignupResult()
    data class FailWithData(val throwable: Throwable?, val signupData: OTSignup): OTSignupResult()
    data class Failure(val throwable: Throwable?): OTSignupResult()
}