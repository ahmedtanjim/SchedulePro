package com.shiftboard.schedulepro.activities.splash.repo

import android.util.Log
import com.google.gson.GsonBuilder
import com.shiftboard.schedulepro.activities.auth.repo.AuthRepo
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.core.common.MobileNotEnabled
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.AbstractExceptionLogger
import com.shiftboard.schedulepro.core.common.analytics.UserActionAnalyticEvents
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.ApiPermissions
import com.shiftboard.schedulepro.core.network.model.Organization
import com.shiftboard.schedulepro.core.persistence.model.UserPrefs
import com.shiftboard.schedulepro.core.persistence.preference.PermissionRepo
import com.shiftboard.schedulepro.core.persistence.preference.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class UserCacheRepo(
    private val api: ScheduleProApi,
    private val authRepo: AuthRepo,
    private val userRepo: UserPreferences,
    private val permissionsRepo: PermissionRepo,
    private val analytics: AbstractAnalyticsProvider,
    private val errorLogger: AbstractExceptionLogger,
    networkCallImpl: BaseNetworkCall
) : BaseRepository(networkCallImpl) {

    private suspend fun checkOrg(): Organization {
        return when (val orgResults = networkCall { api.fetchOrganization() }) {
            is Maybe.Success -> {
                if (orgResults.data.allowMobileAccess) {
                    analytics.setCustomProperty("organization_id", orgResults.data.id)
                    errorLogger.setCustomProperty("organization_id", orgResults.data.id)

                    AppPrefs.militaryTime = orgResults.data.is24HourClockTime
                    AppPrefs.orgID = orgResults.data.id
                    orgResults.data
                } else {
                    throw MobileNotEnabled()
                }
            }
            is Maybe.Failure -> throw orgResults.exception ?: NetworkError("Unknown")
        }
    }

    suspend fun permissions(): ApiPermissions {
        return when (val permissionResults = networkCall { api.permissions() }) {
            is Maybe.Success -> {
                permissionsRepo.updateUser(permissionResults.data)
                permissionResults.data
            }
            is Maybe.Failure -> throw permissionResults.exception ?: NetworkError("Unknown")
        }
    }

    private suspend fun cacheUserData(): UserPrefs {
        return when (val result = networkCall { api.me() }) {
            is Maybe.Success -> {
                val user = result.data.map()
                val res = api.me()
                Log.d("cacheUserData", ":$res}")

                analytics.setUserId(user.id)
                errorLogger.setUserId(user.id)

                analytics.setCustomProperty("group_id", user.groupId ?: "")
                errorLogger.setCustomProperty("group_id", user.groupId ?: "")

                analytics.setCustomProperty("team_id", user.teamId ?: "")
                errorLogger.setCustomProperty("team_id", user.teamId ?: "")

                userRepo.updateUser(user)

                CredentialPrefs.userId = result.data.id
                CredentialPrefs.firstName = result.data.firstName
                CredentialPrefs.lastName = result.data.lastName
                user
            }
            is Maybe.Failure -> throw result.exception ?: NetworkError("Unknown")
        }
    }

    suspend fun cacheUser(): Maybe<UserPrefs> {
        return try {
            withContext(Dispatchers.IO) {
                val call1 = async { checkOrg() }
                val call2 = async { permissions() }
                val call3 = async { cacheUserData() }

                call1.await()
                call2.await()
                val userData = call3.await()

                analytics.logEvent(UserActionAnalyticEvents.Login)
                analytics.logEvent(UserActionAnalyticEvents.Silent_Login)
                Maybe.Success(userData)
            }
        } catch (e: Exception) {
            Maybe.Failure(e)
        }
    }

    suspend fun saveLogInEvent(): Maybe<Boolean> {
        return authRepo.postLogInEvent()
    }
}