package com.shiftboard.schedulepro.core.persistence.model

import org.threeten.bp.OffsetDateTime

data class UserPrefs(
    val id: String,
    val employeeNumber: String,

    val firstName: String,
    val lastName: String,

    val teamId: String?,
    val groupId: String?,

    val phone: String,
    val email: String,
    val address: String,
    val city: String,
    val state: String,

    val secondaryPhone: String,
    val secondaryEmail: String,

    val emergencyContactName: String,
    val emergencyContactPhone: String,

    val cachedAt: OffsetDateTime
)