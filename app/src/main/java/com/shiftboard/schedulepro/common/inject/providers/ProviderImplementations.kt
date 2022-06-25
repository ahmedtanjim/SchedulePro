package com.shiftboard.schedulepro.common.inject.providers

import com.shiftboard.schedulepro.activities.root.repo.SharedRepo
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.common.prefs.CredentialPrefs
import com.shiftboard.schedulepro.core.messaging.FcmTokenInterface
import com.shiftboard.schedulepro.core.messaging.FcmUserValidator
import com.shiftboard.schedulepro.core.messaging.MessageContent
import com.shiftboard.schedulepro.core.network.common.NetworkCredentialProvider
import com.shiftboard.schedulepro.core.persistence.DatabaseCredentialsProvider
import net.openid.appauth.AuthState

class DatabaseCredentialsProviderImpl : DatabaseCredentialsProvider {
    override fun databaseCredentials(): String = CredentialPrefs.databaseToken
}

class FcmTokenInterfaceImpl: FcmTokenInterface {
    override var fcmToken: String = AppPrefs.fcmToken
    override var tokenSynced: Long = AppPrefs.tokenSynced
}

class FcmUserValidatorImpl(private val sharedRepo: SharedRepo): FcmUserValidator {
    override suspend fun isUserValid(message: MessageContent): Boolean {
        return sharedRepo.checkNotificationUser(message.user_id ?: "")
    }
}

class NetworkCredentialProviderImpl: NetworkCredentialProvider {
    override fun getToken(): AuthState = CredentialPrefs.keyState
    override fun updateToken(authState: AuthState) {
        CredentialPrefs.keyState = authState
    }

    override fun getApiKey(): String = AppPrefs.environment.apiKey
    override fun getServerUrl(): String = AppPrefs.environment.baseUrl
    override fun getBasePath(): String = AppPrefs.environment.basePath
}