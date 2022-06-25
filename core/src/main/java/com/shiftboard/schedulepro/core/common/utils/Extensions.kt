@file:Suppress("unused")
package com.shiftboard.schedulepro.core.common.utils

import android.graphics.Color

/**
 * Generic extensions
 */

val <T> T.exhaustive: T get() = this

fun <K, V> Map<K, V>.flipKeyValue(): Map<V, K> = entries.associate { it.value to it.key }

fun <K, V> Map<K, V>.firstKeyWithValue(value: V): K? = entries.firstOrNull { it.value == value }?.key

fun <T: Collection<*>> T.takeIfNotEmpty() = takeIf { it.isNotEmpty() }
fun <T: Collection<*>> T.takeIfEmpty() = takeIf { it.isEmpty() }

fun <R> String?.letIfNotBlank(block: (String) -> R) = takeIf { !isNullOrBlank() }?.let(block)

inline fun <T> applyToEach(vararg values: T, block: T.()->Unit) = values.forEach { it.apply(block) }

fun parseAsColor(colorString: String?): Int {
    return if (colorString.isNullOrBlank()) {
        Color.WHITE
    } else {
        try {
            Color.parseColor(colorString)
        } catch (e: Exception) {
            Color.WHITE
        }
    }
}