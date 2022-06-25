package com.shiftboard.schedulepro.core.network.model.profile

import com.shiftboard.schedulepro.core.persistence.Mappable
import com.shiftboard.schedulepro.core.persistence.model.UserPrefs
import com.squareup.moshi.JsonClass
import org.threeten.bp.OffsetDateTime


@JsonClass(generateAdapter = true)
data class UserData(
    val id: String,
    val employeeNumber: String,

    val teamId: String? = null,
    val groupId: String? = null,

    val firstName: String = "",
    val lastName: String = "",

    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",

    val secondaryPhone: String = "",
    val secondaryEmail: String = "",

    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
) : Mappable<UserPrefs> {
    override fun map(cachedAt: OffsetDateTime): UserPrefs {
        return UserPrefs(
            id = id,
            employeeNumber = employeeNumber,
            teamId = teamId,
            groupId = groupId,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            address = address,
            city = city,
            state = state,
            secondaryPhone = secondaryPhone,
            secondaryEmail = secondaryEmail,
            emergencyContactName = emergencyContactName,
            emergencyContactPhone = emergencyContactPhone,
            cachedAt = cachedAt
        )
    }
}