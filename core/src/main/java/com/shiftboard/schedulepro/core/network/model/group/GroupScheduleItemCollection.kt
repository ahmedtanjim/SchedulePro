package com.shiftboard.schedulepro.core.network.model.group

import com.shiftboard.schedulepro.core.network.model.schedule.ScheduleElementModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupScheduleItemCollection (
    val id: String,
    val employeeName: String,
    val scheduleItemCollections: List<ScheduleItemCollection>
)