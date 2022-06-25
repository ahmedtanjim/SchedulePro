@file:Suppress("unused", "MemberVisibilityCanBePrivate")
package com.shiftboard.schedulepro.ui.lineitem


import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannedString
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.shiftboard.schedulepro.ui.R

class TextAreaLineItemWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
): ConstraintLayout(context, attrs, defStyle) {
    private val icon: ImageView
    private val value: TextView

    init {
        View.inflate(context, R.layout.widget_text_area_line_item, this)

        icon = findViewById(R.id.icon)
        value = findViewById(R.id.value)

        context.obtainStyledAttributes(attrs, R.styleable.TextAreaLineItemWidget).use {
            it.getResourceId(R.styleable.TextAreaLineItemWidget_android_icon, 0)
                .takeIf { res -> res != 0 }?.let { res -> setIcon(res) }
        }
    }

    fun setText(text: String) {
        value.text = text
    }

    fun setText(text: SpannedString) {
        value.text = text
    }

    fun setIcon(drawable: Drawable?) {
        icon.setImageDrawable(drawable)
    }

    fun setIcon(@DrawableRes res: Int) {
        icon.setImageResource(res)
    }
}