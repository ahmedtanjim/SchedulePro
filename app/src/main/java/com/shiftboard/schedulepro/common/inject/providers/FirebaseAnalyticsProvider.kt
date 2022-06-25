package com.shiftboard.schedulepro.common.inject.providers

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.AnalyticEvent
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.core.common.utils.bundleOf

class FirebaseAnalyticsProvider(val firebase: FirebaseAnalytics) : AbstractAnalyticsProvider() {
    override fun logEvent(name: String, params: Bundle?) {
        firebase.logEvent(name, params)
    }

    override fun logEvent(event: AnalyticEvent) {
        var bundle = event.bundle
        if (bundle != null) {
            bundle.putString("org_id", AppPrefs.orgID)
        } else {
            bundle = bundleOf("org_id" to AppPrefs.orgID)
        }
        firebase.logEvent(event.eventName, bundle)
    }

    override fun resetAnalyticsData() {
        firebase.resetAnalyticsData()
    }

    override fun pageViewEvent(event: PageViewEvent) {
        firebase.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to event.screenName,
                FirebaseAnalytics.Param.SCREEN_CLASS to event.parentActivity
            )
        )
    }

    override fun setAnalyticsCollectionEnabled(enabled: Boolean) {
        firebase.setAnalyticsCollectionEnabled(enabled)
    }

    override fun setCurrentScreen(
        activity: Activity,
        screenName: String,
        screenClassOverride: String?,
    ) {
        firebase.setCurrentScreen(activity, screenName, screenClassOverride)
    }

    override fun setSessionTimeoutDuration(milliseconds: Long) {
        firebase.setSessionTimeoutDuration(milliseconds)
    }

    override fun setUserId(id: String) {
        firebase.setUserId(id)
    }

    override fun setCustomProperty(key: String, value: String) {
        firebase.setUserProperty(key, value)
    }
}