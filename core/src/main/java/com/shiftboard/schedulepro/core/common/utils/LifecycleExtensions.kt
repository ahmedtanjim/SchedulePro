@file:Suppress("unused")
package com.shiftboard.schedulepro.core.common.utils

@Suppress("MemberVisibilityCanBePrivate")
open class Event<out T>(private val content: T, handled: Boolean = false) {

    var hasBeenHandled = handled
        private set // Allow external read but not write

    fun doUnlessHandled(block: (T?) -> Unit) {
        if (hasBeenHandled) {
            return
        } else {
            hasBeenHandled = true
            return block(content)
        }
    }

    fun doUnlessHandledOrNull(block: (T) -> Unit) {
        if (hasBeenHandled || content == null) {
            return
        } else {
            hasBeenHandled = true
            return block(content)
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

