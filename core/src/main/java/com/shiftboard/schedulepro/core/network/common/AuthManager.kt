package com.shiftboard.schedulepro.core.network.common

import com.shiftboard.schedulepro.core.common.AuthException
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.suspendCoroutine


interface NetworkCredentialProvider {
    fun getToken(): AuthState
    fun updateToken(authState: AuthState)
    fun getApiKey(): String
    fun getServerUrl(): String
    fun getBasePath(): String
}

class TokenRefresher(
    private val credentialProvider: NetworkCredentialProvider,
) {
    suspend fun refreshToken() {
        suspendCoroutine<String> {
            val authState = credentialProvider.getToken()
            if (!authState.needsTokenRefresh) {
                it.resumeWith(authState.idToken?.let { token -> Result.success(token) }
                    ?: Result.failure(AuthException()))
                return@suspendCoroutine
            }

            if (authState.refreshToken.isNullOrBlank()) {
                it.resumeWith(Result.failure(AuthException()))
                return@suspendCoroutine
            }

            val tokenRequest = authState.createTokenRefreshRequest()
            val requestBody = FormBody.Builder().apply {
                tokenRequest.requestParameters.forEach { (key, value) ->
                    add(key, value)
                }
                add(TokenRequest.PARAM_CLIENT_ID, tokenRequest.clientId)
            }.build()

            val requestCall = Request.Builder()
                .url(tokenRequest.configuration.tokenEndpoint.toString())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build()

            val okHttpClient = OkHttpClient.Builder().build()
            okHttpClient.newCall(requestCall).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWith(Result.failure(AuthException()))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        // Double check that we got a successful response
                        it.resumeWith(Result.failure(AuthException()))
                        return
                    }
                    val tokenResponse = try {
                        TokenResponse.Builder(tokenRequest)
                            .fromResponseJsonString(response.body?.string() ?: "").build()
                    } catch (e: Exception) {
                        // This happens if we don't get back a valid token e.g. if the `token_type` is missing.
                            // for some reason this happens occasionally
                        it.resumeWith(Result.failure(AuthException()))
                        return
                    }

                    credentialProvider.updateToken(credentialProvider.getToken().apply {
                        update(tokenResponse, null)
                    })
                    it.resumeWith(authState.idToken?.let { token -> Result.success(token) }
                        ?: Result.failure(AuthException()))
                }
            })
        }
    }
}

suspend fun AuthorizationService.proceedWithFreshToken(authState: AuthState) =
    suspendCoroutine<String> {
        if (!authState.needsTokenRefresh) {
            it.resumeWith(authState.accessToken?.let { token -> Result.success(token) }
                ?: Result.failure(AuthException()))
        }

        authState.performActionWithFreshTokens(this) { accessToken, _, ex ->
            if (ex == null) {
                it.resumeWith(accessToken?.let { token -> Result.success(token) } ?: Result.failure(
                    AuthException()))
            } else {
                it.resumeWith(Result.failure(AuthException()))
            }
        }
    }