package com.shiftboard.schedulepro.content.trade.domain.repository

import com.shiftboard.schedulepro.core.network.model.TradeShift

interface TradeRepository {
    fun getAllShifts(): List<TradeShift>
    fun getAllEmployees(): List<TradeShift>
}