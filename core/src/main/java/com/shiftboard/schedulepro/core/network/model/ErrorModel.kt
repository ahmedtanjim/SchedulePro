package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ErrorModel(
    val status: Int = 0,
    val title: String = "",
    val message: String = ""
)