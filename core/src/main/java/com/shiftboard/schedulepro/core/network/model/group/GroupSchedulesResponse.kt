package com.shiftboard.schedulepro.core.network.model.group

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupSchedulesResponse(
    val groupScheduleItemCollections: List<GroupScheduleItemCollection>
)