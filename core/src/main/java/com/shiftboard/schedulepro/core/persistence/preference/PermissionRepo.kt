package com.shiftboard.schedulepro.core.persistence.preference

import android.content.Context
import androidx.datastore.CorruptionException
import androidx.datastore.DataStore
import androidx.datastore.Serializer
import androidx.datastore.createDataStore
import androidx.datastore.handlers.ReplaceFileCorruptionHandler
import com.google.protobuf.InvalidProtocolBufferException
import com.shiftboard.schedulepro.core.network.model.ApiPermissions
import com.shiftboard.schedulepro.core.persistence.model.PermissionPrefs
import com.shiftboard.schedulepro.core.proto.PermissionPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class PermissionRepo(context: Context) {
    private val datastore: DataStore<PermissionPreference> =
        context.createDataStore("permission_preferences.pb", PermissionSerializer,
            corruptionHandler = ReplaceFileCorruptionHandler {
                PermissionPreference.getDefaultInstance()
            })

    suspend fun updateUser(prefs: PermissionPrefs) {
        datastore.updateData {
            it.toBuilder()
                .setCreateLeave(prefs.createLeave)
                .build()
        }
    }

    suspend fun updateUser(prefs: ApiPermissions) {
        datastore.updateData {
            it.toBuilder()
                .setCreateLeave(prefs.leave.create)
                .build()
        }
    }

    suspend fun getCurrent(): PermissionPrefs {
        return datastore.data.catch {
            if (it is IOException) {
                Timber.e("Error reading User Preference.")
                emit(PermissionPreference.getDefaultInstance())
            } else {
                throw it
            }
        }.map { it.mapFromProto() }.first()
    }

    fun observe(): Flow<PermissionPrefs> {
        return datastore.data.catch {
            if (it is IOException) {
                Timber.e("Error reading User Preference.")
                emit(PermissionPreference.getDefaultInstance())
            } else {
                throw it
            }
        }.map { it.mapFromProto() }
    }

    private fun PermissionPreference.mapFromProto(): PermissionPrefs {
        return PermissionPrefs(
            createLeave = createLeave
        )
    }

    object PermissionSerializer : Serializer<PermissionPreference> {
        override fun readFrom(input: InputStream): PermissionPreference {
            try {
                return PermissionPreference.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override fun writeTo(t: PermissionPreference, output: OutputStream) {
            t.writeTo(output)
        }
    }
}