@file:Suppress("MemberVisibilityCanBePrivate")

package com.shiftboard.schedulepro.core.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.shiftboard.schedulepro.core.R
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import org.koin.android.ext.android.inject


abstract class BaseFragment: Fragment() {
    @get:StringRes
    abstract val titleRes: Int
    @get:LayoutRes
    abstract val layoutRes: Int

    abstract fun createAnalyticsEvent(): PageViewEvent

    protected val analytics by inject<AbstractAnalyticsProvider>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layoutRes, container, false)

    override fun onResume() {
        super.onResume()
        analytics.pageViewEvent(createAnalyticsEvent())
    }

    open fun getTitle() = getString(titleRes)

    fun tryWithNav(block: NavController.()->Unit) {
        try {
            block(NavHostFragment.findNavController(this))
        } catch (e: Exception) {

        }
    }
}