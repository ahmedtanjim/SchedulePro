package com.shiftboard.schedulepro.core.network.model.trade

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TradeRequest(
    val originEmployeeId: String,
    val originShiftIds: List<String>,
    val recipientEmployeeId: String,
    val recipientShiftIds: List<String>
)

@JsonClass(generateAdapter = true)
data class TradeValidationResponse(
    val originViolations: Map<String,List<String>>,
    val recipientViolations: Map<String,List<String>>,
    val canSubmitTrade: Boolean
)