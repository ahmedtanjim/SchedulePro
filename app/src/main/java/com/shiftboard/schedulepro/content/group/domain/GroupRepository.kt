package com.shiftboard.schedulepro.content.group.domain

import com.shiftboard.schedulepro.core.network.model.TradeShift

interface GroupRepository {
    fun getSchedules(): List<TradeShift>
    fun getFilters(): List<TradeShift>
}