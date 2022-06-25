package com.shiftboard.schedulepro.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiPermissions(
    val leave: LeavePermissions,
    val groupSchedule: GroupPermissions,
    val trade: TradePermissions
)

@JsonClass(generateAdapter = true)
data class LeavePermissions(
    val create: Boolean = false
)

@JsonClass(generateAdapter = true)
data class GroupPermissions(
    val read: Boolean = false
)

@JsonClass(generateAdapter = true)
data class TradePermissions(
    val create: Boolean = false
)