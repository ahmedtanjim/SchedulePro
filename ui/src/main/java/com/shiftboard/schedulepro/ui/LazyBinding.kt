package com.shiftboard.schedulepro.ui

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class LazyViewBinding<T : ViewBinding>(val fragment: Fragment, var binder: () -> T) :
    ReadOnlyProperty<Fragment, T>,
    LifecycleObserver {
    private var lifecycle: Lifecycle? = null
    private var binding: T? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyView() {
        lifecycle?.removeObserver(this) // not mandatory, but preferred
        lifecycle = null
        binding = null
    }

    private fun fetchBinding(): T {
        lifecycle = fragment.viewLifecycleOwner.lifecycle

        if (lifecycle == null || !lifecycle!!.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw BindingLifecycleException()
        }

        if (binding == null) {
            binding = binder()
            lifecycle?.addObserver(this)
        }
        return binding
            ?: throw BindingLifecycleException()
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return fetchBinding()
    }
}

class BindingLifecycleException: IllegalStateException("Accessing binding outside of fragment lifecycle")

inline fun <reified T : ViewBinding> initBinding(fragment: Fragment): T {
    val clazz = T::class.java
    val method = clazz.getDeclaredMethod("bind", View::class.java)
    return method.invoke(null, fragment.view) as T
}

inline fun <reified T : ViewBinding> Fragment.lazyViewBinding() =
    LazyViewBinding(this) { initBinding<T>(this) }

inline fun <reified T : ViewBinding> tryWith(binding: T, block: T.()->Unit) {
    try {
        block(binding)
    } catch (e: BindingLifecycleException) {
        // Squash
    }
}