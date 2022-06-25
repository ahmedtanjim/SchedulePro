package com.shiftboard.schedulepro.ui.form


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shiftboard.schedulepro.ui.R


abstract class BaseFormView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    protected abstract val formIcon: ImageView
    protected abstract val formText: TextInputEditText
    protected abstract val formTextContainer: TextInputLayout

    protected var errorText: String? = null

    fun setIcon(@DrawableRes res: Int) {
        formIcon.setImageResource(res)
    }

    fun setIcon(drawable: Drawable) {
        formIcon.setImageDrawable(drawable)
    }

    fun setIconEnabled(enabled: Boolean) {
        formIcon.isVisible = enabled
    }

    fun setTitle(charSequence: CharSequence?) {
        formTextContainer.hint = charSequence
        formTextContainer.isHintEnabled = !charSequence.isNullOrEmpty()
    }

    fun setTitleTextColor(colorStateList: ColorStateList) {
        formTextContainer.hintTextColor = colorStateList
    }

    fun setTitleEnabled(enabled: Boolean) {
        formTextContainer.isHintEnabled = enabled
    }

    fun setRequired(required: Boolean) {
        if (required) {
            formTextContainer.helperText = context.getString(R.string.required)
        } else {
            formTextContainer.helperText = null
        }
    }

    fun setText(text: String) {
        formText.setText(text)
    }
    fun setHtmlText(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY))
        } else {
            @Suppress("DEPRECATION")
            formText.setText(Html.fromHtml(text))
        }
    }

    fun setTitle(@StringRes res: Int) = setTitle(context.getText(res))
    fun setErrorText(@StringRes res: Int) = setErrorText(context.getText(res))

    fun setErrorText(charSequence: CharSequence?) {
        errorText = charSequence?.toString()
    }

    fun setErrorTextColor(colorStateList: ColorStateList) {
        formTextContainer.setErrorTextColor(colorStateList)
    }

    fun setErrorTextEnabled(enabled: Boolean) {
        formTextContainer.isErrorEnabled = enabled
        if (enabled) {
            formTextContainer.error = errorText
        }
    }

    open fun clear() {
        formText.setText("")
        setErrorTextEnabled(false)
    }
}