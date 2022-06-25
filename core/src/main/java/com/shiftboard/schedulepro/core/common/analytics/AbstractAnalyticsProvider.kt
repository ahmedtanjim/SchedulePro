package com.shiftboard.schedulepro.core.common.analytics

import android.app.Activity
import android.os.Bundle

abstract class AbstractAnalyticsProvider: AnalyticsProperties {
    abstract fun logEvent(name: String, params: Bundle?)
    abstract fun logEvent(event: AnalyticEvent)
    abstract fun resetAnalyticsData()
    abstract fun pageViewEvent(event: PageViewEvent)
    abstract fun setAnalyticsCollectionEnabled(enabled: Boolean)
    abstract fun setCurrentScreen(activity: Activity, screenName: String, screenClassOverride: String? = null)
    abstract fun setSessionTimeoutDuration(milliseconds: Long)
}