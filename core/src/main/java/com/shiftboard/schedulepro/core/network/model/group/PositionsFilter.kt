package com.shiftboard.schedulepro.core.network.model.group

data class PositionsFilter(
    val admin: Boolean,
    val csa: Boolean,
    val dev: Boolean,
    val imp: Boolean,
    val op: Boolean,
    val pmt: Boolean,
)
