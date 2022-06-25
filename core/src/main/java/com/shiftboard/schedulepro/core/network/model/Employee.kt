package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Employee(
    val id: String,
    val firstName: String,
    val lastName: String
)
