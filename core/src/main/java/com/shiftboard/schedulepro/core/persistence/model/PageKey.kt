package com.shiftboard.schedulepro.core.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PageKey(
    @PrimaryKey val pageName: String,
    val nextKey: String?,
    val prevKey: String?
)