package com.shiftboard.schedulepro.content.trade.screens.main

import androidx.lifecycle.ViewModel
import com.shiftboard.schedulepro.content.trade.screens.main.TradeScreenState
import com.shiftboard.schedulepro.core.network.model.TradeShift
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainScreenViewModel : ViewModel() {
    private val _screenState = MutableStateFlow(TradeScreenState.Trade)
    val screenState: StateFlow<TradeScreenState> = _screenState

    fun changeScreenState(state: TradeScreenState){
        _screenState.value = state
    }

    private val _selectedTrade = MutableStateFlow<TradeShift?>(null)
    val selectedTrade: StateFlow<TradeShift?> = _selectedTrade

    fun changeSelectedShift(shift: TradeShift){
        _selectedTrade.value = shift
    }

}