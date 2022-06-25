package com.shiftboard.schedulepro.core.network.common

import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.ServerError
import com.shiftboard.schedulepro.core.network.model.ErrorModel
import com.squareup.moshi.Moshi
import okhttp3.Response

class ResultInterceptor(private val moshi: Moshi): BasicRequestInterceptor() {
    override fun processResponse(response: Response): Response {
        if (!response.isSuccessful) {
            val adapter = moshi.adapter(ErrorModel::class.java)
            val json = adapter.fromJson(
                response.body?.string() ?: throw EmptyBodyException()
            ) ?: throw EmptyBodyException()

            throw ServerError(json.status, json.title, json.message)
        }
        return super.processResponse(response)
    }
}