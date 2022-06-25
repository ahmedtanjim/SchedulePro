package com.shiftboard.schedulepro.core.common.analytics

interface AnalyticsProperties {
    abstract fun setUserId(id: String)
    abstract fun setCustomProperty(key: String, value: String)
}