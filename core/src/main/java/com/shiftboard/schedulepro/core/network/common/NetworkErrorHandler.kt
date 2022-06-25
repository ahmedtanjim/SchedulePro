package com.shiftboard.schedulepro.core.network.common

import android.content.Context
import androidx.annotation.StringRes
import com.shiftboard.schedulepro.core.common.AuthException
import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.ServerError
import com.shiftboard.schedulepro.core.common.analytics.AbstractExceptionLogger
import com.shiftboard.schedulepro.core.common.utils.Event
import com.shiftboard.schedulepro.core.network.model.ErrorModel
import com.squareup.moshi.Moshi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.net.UnknownHostException

class NetworkErrorHandler(
    val context: Context,
    val logger: AbstractExceptionLogger,
    val moshi: Moshi
) {
    private val _criticalErrorChannel: ConflatedBroadcastChannel<Event<Throwable>> = ConflatedBroadcastChannel()
    val criticalErrors = _criticalErrorChannel.asFlow()

    fun handleServerError(response: Response): Response {
        try {
            val adapter = moshi.adapter(ErrorModel::class.java)
            val json = adapter.fromJson(
                response.body?.string() ?: throw EmptyBodyException()
            ) ?: throw EmptyBodyException()

            throw ServerError(json.status, json.title, json.message)
        } catch (e: ServerError) {
            // we actually want this to throw
            throw e
        }
        catch (e: Exception) {
            // our error didn't parse correctly so we just want to ignore it
        }

        return response
    }

    suspend fun postAuthError(authException: AuthException) {
        if (!_criticalErrorChannel.isClosedForSend) {
            _criticalErrorChannel.send(Event(authException))
        }
    }

    fun getFriendlyMessage() {

    }
}