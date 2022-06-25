package com.shiftboard.schedulepro.core.network.common

import android.util.Log
import okhttp3.Request


class UrlInterceptor(private val credentialProvider: NetworkCredentialProvider): BasicRequestInterceptor() {

    override fun processRequest(request: Request): Request {
        val url = request.url.newBuilder()
            .host(credentialProvider.getServerUrl())
            .encodedPath(credentialProvider.getBasePath() + request.url.encodedPath)
            .build()

        return request.newBuilder()
            .url(url)
            .build()
    }
}

class AuthInterceptor(private val credentialProvider: NetworkCredentialProvider): BasicRequestInterceptor() {

    override fun processRequest(request: Request): Request {
        return request.newBuilder()
            .also {
                val token = credentialProvider.getToken().idToken
                Log.d("processRequest", ": $token")
                Log.d("processRequest-key", ": ${credentialProvider.getApiKey()}")
                it.addHeader("Authorization", "Bearer $token")
                it.addHeader("Ocp-Apim-Subscription-Key", credentialProvider.getApiKey())
            }
            .build()
    }
}