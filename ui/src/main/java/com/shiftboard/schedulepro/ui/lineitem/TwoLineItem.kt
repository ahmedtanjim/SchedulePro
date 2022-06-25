@file:Suppress("MemberVisibilityCanBePrivate", "unused")
package com.shiftboard.schedulepro.ui.lineitem

import android.content.Context
import android.text.SpannedString
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import com.shiftboard.schedulepro.ui.R

class TwoLineItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
): ConstraintLayout(context, attrs, defStyle) {
    private val title: TextView
    private val value: TextView

    init {
        View.inflate(context, R.layout.widget_two_line_item, this)

        title = findViewById(R.id.title)
        value = findViewById(R.id.value)

        context.obtainStyledAttributes(attrs, R.styleable.TwoLineItem).use {
            it.getString(R.styleable.TwoLineItem_android_text)?.let { text -> setText(text) }
            it.getString(R.styleable.TwoLineItem_android_title)?.let { text -> setTitle(text) }
        }
    }

    fun setTitle(@StringRes res: Int) {
        setTitle(context.getString(res))
    }

    fun setTitle(text: String) {
        title.text = text
    }

    fun setText(@StringRes res: Int) {
        setText(context.getString(res))
    }

    fun setText(text: String) {
        value.text = text
    }

    fun setText(text: SpannedString) {
        value.setText(text, TextView.BufferType.SPANNABLE)
    }

    fun setTextIfNotNull(text: String?) {
        if (text.isNullOrBlank()) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
            value.text = text
        }
    }
}