package com.shiftboard.schedulepro.content.overtime

import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.common.utils.serverFormat
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignup
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignupPost
import com.shiftboard.schedulepro.core.network.model.overtime.OTSignupResult
import org.threeten.bp.LocalDate


class SignupRepo(
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall,
) : BaseRepository(networkCallImpl) {

    suspend fun fetchSignup(date: LocalDate): Maybe<OTSignup> {
        return networkCall { api.fetchSignupForDate(date.serverFormat()) }
    }

    suspend fun postSignup(data: OTSignupPost): OTSignupResult {
        return when (val result =
            networkCall { api.postSignupUpdate(data.date.serverFormat(), data) }) {
            // successfully posted
            is Maybe.Success -> OTSignupResult.Success(data.date)
            is Maybe.Failure -> {
                when (val res = networkCall { api.fetchSignupForDate(data.date.serverFormat()) }) {
                    is Maybe.Success -> OTSignupResult.FailWithData(result.exception, res.data)
                    is Maybe.Failure -> OTSignupResult.Failure(result.exception)
                }
            }
        }
    }
}

