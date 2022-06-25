package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Page<T>(
    val count: Int,
    val pageSize: Int,
    val totalPages: Int,
    val pageNumber: Int,
    val nextPageNumber: Int? = null,
    val data: List<T>
)