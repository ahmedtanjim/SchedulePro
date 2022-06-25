package com.shiftboard.schedulepro.content.trade.domain.repository

import com.shiftboard.schedulepro.core.network.model.Employee
import com.shiftboard.schedulepro.core.network.model.TradeShift
import com.shiftboard.schedulepro.core.common.EmptyBodyException
import com.shiftboard.schedulepro.core.common.NetworkError
import com.shiftboard.schedulepro.core.common.base.BaseNetworkCall
import com.shiftboard.schedulepro.core.common.base.BaseRepository
import com.shiftboard.schedulepro.core.network.api.ScheduleProApi
import com.shiftboard.schedulepro.core.network.common.Maybe
import com.shiftboard.schedulepro.core.network.model.TradeDetailsElement
import com.shiftboard.schedulepro.core.network.model.details.ShiftDetailsModel
import com.shiftboard.schedulepro.core.network.model.trade.TradeRequest
import com.shiftboard.schedulepro.core.network.model.trade.TradeValidationResponse
import com.shiftboard.schedulepro.core.persistence.model.UserPrefs
import com.shiftboard.schedulepro.core.persistence.preference.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TradesRepo(
    private val api: ScheduleProApi,
    networkCallImpl: BaseNetworkCall,
    private val userRepo: UserPreferences,
) : BaseRepository(networkCallImpl) {

    fun userDetails(): Flow<UserPrefs> = userRepo.observe()


    suspend fun getTradableEmployees(employeeId: String): Maybe<List<Employee>> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.getTradableEmplyoees(employeeId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun getTradableShifts(employeeId: String, shiftDate: String): Maybe<List<TradeShift>> {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.getTradableShiftsForEmployee(employeeId, shiftDate = shiftDate)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body.tradeShifts)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun getShiftDetails(shiftId: String) :Maybe<ShiftDetailsModel>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.fetchShiftDetails(shiftId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }


    suspend fun getTradeDetails(tradeId: String) :Maybe<TradeDetailsElement>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.getTradeDetails(tradeId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }
    suspend fun validateTrade(tradeRequest: TradeRequest): Maybe<TradeValidationResponse>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.validateTrade(tradeRequest)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }
    suspend fun postTrade(tradeRequest: TradeRequest): Maybe<String>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.submitTrade(tradeRequest)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun acceptTrade(tradeId: String): Maybe<Boolean>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.acceptTrade(tradeId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun declineTrade(tradeId: String): Maybe<Boolean>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.declineTrade(tradeId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }

    suspend fun cancelTrade(tradeId: String): Maybe<Boolean>  {
        try {
            return withContext(Dispatchers.IO) {
                val results = api.cancelTrade(tradeId)
                if (!results.isSuccessful) {
                    return@withContext Maybe.Failure(NetworkError(results.message()))
                }
                val body = results.body() ?: return@withContext Maybe.Failure(EmptyBodyException())
                return@withContext Maybe.Success(body)
            }
        } catch (e: Exception) {
            return Maybe.Failure(e)
        }
    }
}
