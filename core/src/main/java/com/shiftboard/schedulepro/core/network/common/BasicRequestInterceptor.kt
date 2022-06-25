package com.shiftboard.schedulepro.core.network.common

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

abstract class BasicRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain) =
        processResponse(chain.proceed(processRequest(chain.request())))

    open fun processRequest(request: Request): Request = request

    open fun processResponse(response: Response): Response = response
}