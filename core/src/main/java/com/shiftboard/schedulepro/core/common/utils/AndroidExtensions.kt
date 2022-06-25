@file:Suppress("unused")
package com.shiftboard.schedulepro.core.common.utils

/**
 * Extensions for the android. or androidx. libraries
 */

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.FloatRange
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.max
import kotlin.math.pow

fun Context.hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.askPermission(
    permission: String,
    request_id: Int,
    reasoning: (permission: String, request_id: Int) -> Unit,
    has_permission: () -> Unit,
) {
    if (!hasPermission(permission)) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) reasoning(
            permission,
            request_id)
        else ActivityCompat.requestPermissions(this, arrayOf(permission), request_id)
    } else has_permission()
}


fun View.dimen(@DimenRes res: Int): Float = context.dimen(res)
fun Context.dimen(@DimenRes res: Int): Float = resources.dimen(res)
fun Resources.dimen(@DimenRes res: Int): Float = getDimension(res)

fun bundleOf(vararg params: Pair<String, Any>): Bundle {
    val b = Bundle()
    for (p in params) {
        val (k, v) = p
        when (v) {
            is Boolean -> b.putBoolean(k, v)
            is Byte -> b.putByte(k, v)
            is Char -> b.putChar(k, v)
            is Short -> b.putShort(k, v)
            is Int -> b.putInt(k, v)
            is Long -> b.putLong(k, v)
            is Float -> b.putFloat(k, v)
            is Double -> b.putDouble(k, v)
            is String -> b.putString(k, v)
            is CharSequence -> b.putCharSequence(k, v)
            is Parcelable -> b.putParcelable(k, v)
            is java.io.Serializable -> b.putSerializable(k, v)
            is BooleanArray -> b.putBooleanArray(k, v)
            is ByteArray -> b.putByteArray(k, v)
            is CharArray -> b.putCharArray(k, v)
            is DoubleArray -> b.putDoubleArray(k, v)
            is FloatArray -> b.putFloatArray(k, v)
            is IntArray -> b.putIntArray(k, v)
            is LongArray -> b.putLongArray(k, v)
            is Array<*> -> {
                @Suppress("UNCHECKED_CAST")
                when {
                    v.isArrayOf<Parcelable>() -> b.putParcelableArray(k, v as Array<out Parcelable>)
                    v.isArrayOf<CharSequence>() -> b.putCharSequenceArray(k,
                        v as Array<out CharSequence>)
                    v.isArrayOf<String>() -> b.putStringArray(k, v as Array<out String>)
                    else -> throw RuntimeException("Unsupported bundle component (${v.javaClass})")
                }
            }
            is ShortArray -> b.putShortArray(k, v)
            is Bundle -> b.putBundle(k, v)
            else -> throw RuntimeException("Unsupported bundle component (${v.javaClass})")
        }
    }

    return b
}

fun checkMainThread() = check(Looper.myLooper() == Looper.getMainLooper()) {
    "Expected to be called on the main thread but was " + Thread.currentThread().name
}

fun Fragment.showMaterialDialog(block: MaterialDialog.() -> Unit): MaterialDialog? {
    val ctx = context ?: return null
    return MaterialDialog(ctx).show {
        lifecycleOwner(viewLifecycleOwner)
        block()
    }
}

fun Fragment.buildMaterialDialog(block: MaterialDialog.() -> Unit): MaterialDialog? {
    val ctx = context ?: return null
    return MaterialDialog(ctx).apply {
        lifecycleOwner(viewLifecycleOwner)
        block()
    }
}

fun TextInputLayout.getText(): String = editText?.text?.toString() ?: ""
fun TextInputLayout.setText(text: String?) = editText?.setText(text)
fun TextInputLayout.setText(text: Int) = editText?.setText(text)

@ColorInt fun colorAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): Int {
    val alphaInt = (255 * alpha).toInt()
    return ColorUtils.setAlphaComponent(color, alphaInt)
}

fun colorListAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float): ColorStateList {
    return ColorStateList.valueOf(colorAlpha(color, alpha))
}

fun luminance(@ColorInt color: Int): Double {
    fun sRGBtoLin(colorChannel: Double): Double {
        return if (colorChannel <= 0.03928) colorChannel / 12.92 else ((colorChannel + 0.055) / 1.055).pow(
            2.4)
    }
    val lR: Double = sRGBtoLin(Color.red(color) / 255.0)
    val lG: Double = sRGBtoLin(Color.green(color) / 255.0)
    val lB: Double = sRGBtoLin(Color.blue(color) / 255.0)

    return 0.2126 * lR + 0.7152 * lG + 0.0722 * lB
}

fun colorContrast(@ColorInt foreground: Int, @ColorInt background: Int): Double {
    val lumForeground = luminance(foreground)
    val lumBackground = luminance(background)

    val brightest = max(lumForeground, lumBackground)
    val darkest = kotlin.math.min(lumForeground, lumBackground)

    return (brightest + 0.05) / (darkest + 0.05)
}

fun Activity.hideSoftKeyboard() {
    getSystemService(InputMethodManager::class.java)
        .hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
}

fun Menu.itemIds(): List<Int> {
    val results = mutableListOf<Int>()
    forEach { results.add(it.itemId) }
    return results
}