package com.shiftboard.schedulepro.core.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.shiftboard.schedulepro.core.R
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import org.koin.android.ext.android.inject

abstract class BaseFullscreenDialog: DialogFragment() {
    @get:LayoutRes
    abstract val layout: Int

    abstract fun createAnalyticsEvent(): PageViewEvent
    protected val analytics by inject<AbstractAnalyticsProvider>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_SchedulePro_FullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layout, container, false)

    override fun onResume() {
        super.onResume()
        analytics.pageViewEvent(createAnalyticsEvent())
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            window?.setLayout(width, height)
        }
    }
}