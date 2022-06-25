package com.shiftboard.schedulepro.core.network.common

import okhttp3.Request
import retrofit2.Invocation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiVersion(val version: String)

class ApiVersioningInterceptor : BasicRequestInterceptor() {

    override fun processRequest(request: Request): Request {

        // Pull out the Invocation tag and check if there is an ApiVersion annotation on it.
        // If there isn't we just return the original request because its good as is, otherwise save the tag.
        val invocation = request.tag(Invocation::class.java)
        val method = invocation?.method()
        val apiVersion = method?.getAnnotation(ApiVersion::class.java)

        apiVersion ?: return request

        // take the current url and add the api-version query param
        val url = request.url.newBuilder()
            .addQueryParameter("api-version", apiVersion.version)
            .build()

        // create a new request with the new url
        return request.newBuilder()
            .url(url)
            .build()
    }
}