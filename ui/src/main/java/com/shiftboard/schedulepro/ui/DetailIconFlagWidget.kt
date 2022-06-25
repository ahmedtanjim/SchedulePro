@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package com.shiftboard.schedulepro.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams

class DetailIconFlagWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
): FrameLayout(context, attrs, defStyle) {

    private val defaultColor by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)
        typedValue.data
    }

    init {
        View.inflate(context, R.layout.widget_detail_icon_flag, this)
        isVisible = false
    }

    fun addIcon(@DrawableRes res: Int, color: Int? = null) {
        val drawable = ContextCompat.getDrawable(context, res) ?: return
        drawable.mutate().setTint(color ?: defaultColor)
        addIcon(drawable)
    }

    fun addIcon(drawable: Drawable) {
        val imageView = ImageView(context)

        imageView.setPadding(2.dp)
        imageView.updateLayoutParams<LinearLayout.LayoutParams> {
            width = 24.dp
            height = 24.dp
        }
        imageView.setImageDrawable(drawable)

        addView(imageView)

        if (childCount > 0) {
            isVisible = true
        }
    }
}