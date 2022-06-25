package com.shiftboard.schedulepro.core.persistence.preference

import android.content.Context
import androidx.datastore.CorruptionException
import androidx.datastore.DataStore
import androidx.datastore.Serializer
import androidx.datastore.createDataStore
import androidx.datastore.handlers.ReplaceFileCorruptionHandler
import com.google.protobuf.InvalidProtocolBufferException
import com.shiftboard.schedulepro.core.common.utils.toOffsetDateTime
import com.shiftboard.schedulepro.core.common.utils.toProto
import com.shiftboard.schedulepro.core.persistence.model.UserPrefs
import com.shiftboard.schedulepro.core.proto.UserPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class UserPreferences(context: Context) {
    private val datastore: DataStore<UserPreference> = context.createDataStore("user_preferences.pb", UserSerializer,
        corruptionHandler = ReplaceFileCorruptionHandler {
            UserPreference.getDefaultInstance()
        })

    suspend fun updateUser(user: UserPrefs) {
        datastore.updateData {
            it.toBuilder()
                .setId(user.id)
                .setEmployeeNumber(user.employeeNumber)
                .setFirstName(user.firstName)
                .setLastName(user.lastName)
                .setTeamId(user.teamId)
                .setGroupId(user.groupId)
                .setPhone(user.phone)
                .setEmail(user.email)
                .setAddress(user.address)
                .setCity(user.city)
                .setState(user.state)
                .setSecondaryPhone(user.secondaryPhone)
                .setSecondaryEmail(user.secondaryEmail)
                .setEmergencyContactName(user.emergencyContactName)
                .setEmergencyContactPhone(user.emergencyContactPhone)
                .setCachedAt(user.cachedAt.toProto())
                .build()
        }
    }

    suspend fun getCurrent(): UserPrefs {
        return datastore.data.catch {
            if (it is IOException) {
                Timber.e("Error reading User Preference.")
                emit(UserPreference.getDefaultInstance())
            } else {
                throw it
            }
        }.map { it.mapFromProto() }.first()
    }

    fun observe(): Flow<UserPrefs> {
        return datastore.data.catch {
            if (it is IOException) {
                Timber.e("Error reading User Preference.")
                emit(UserPreference.getDefaultInstance())
            } else {
                throw it
            }
        }.map { it.mapFromProto() }
    }

    private fun UserPreference.mapFromProto(): UserPrefs {
        return UserPrefs(
            id = id,
            employeeNumber = employeeNumber,
            firstName = firstName,
            lastName = lastName,
            teamId = teamId,
            groupId = groupId,
            phone = phone,
            email = email,
            address = address,
            city = city,
            state = state,
            secondaryPhone = secondaryPhone,
            secondaryEmail = secondaryEmail,
            emergencyContactName = emergencyContactName,
            emergencyContactPhone = emergencyContactPhone,
            cachedAt = cachedAt.toOffsetDateTime()
        )
    }

    object UserSerializer : Serializer<UserPreference> {
        override fun readFrom(input: InputStream): UserPreference {
            try {
                return UserPreference.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override fun writeTo(t: UserPreference, output: OutputStream) {
            t.writeTo(output)
        }
    }
}