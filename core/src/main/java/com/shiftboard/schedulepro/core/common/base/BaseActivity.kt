package com.shiftboard.schedulepro.core.common.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.shiftboard.schedulepro.core.common.analytics.AbstractAnalyticsProvider
import com.shiftboard.schedulepro.core.common.utils.nonNull
import com.shiftboard.schedulepro.core.common.utils.observeNonNull
import com.shiftboard.schedulepro.core.network.common.NetworkErrorHandler
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity

abstract class BaseActivity: AppCompatActivity() {
    val analytics: AbstractAnalyticsProvider by inject()

    protected fun <T> LiveData<T>.observe(observer: (T?) -> Unit) {
        observe(this@BaseActivity, Observer { observer(it) })
    }

    protected fun <T> LiveData<T>.observeNotNull(observer: (T) -> Unit) {
        nonNull().observeNonNull(this@BaseActivity) {
            observer(it)
        }
    }
}

abstract class BaseScopedActivity: ScopeActivity() {
    val analytics: AbstractAnalyticsProvider by inject()

    protected fun <T> LiveData<T>.observe(observer: (T?) -> Unit) {
        observe(this@BaseScopedActivity, Observer { observer(it) })
    }

    protected fun <T> LiveData<T>.observeNotNull(observer: (T) -> Unit) {
        nonNull().observeNonNull(this@BaseScopedActivity) {
            observer(it)
        }
    }
}