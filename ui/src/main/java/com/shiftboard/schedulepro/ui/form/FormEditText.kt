@file:Suppress("MemberVisibilityCanBePrivate", "unused")
package com.shiftboard.schedulepro.ui.form

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shiftboard.schedulepro.core.common.utils.setText
import com.shiftboard.schedulepro.ui.R
import kotlinx.coroutines.flow.Flow
import reactivecircus.flowbinding.android.widget.textChanges

class FormEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val formText by lazy {  findViewById<TextInputEditText>(R.id.text_edit) }

    private val formIcon by lazy {  findViewById<ImageView>(R.id.form_icon) }
    private val formTextContainer by lazy {  findViewById<TextInputLayout>(R.id.text_content) }
    private var errorText: String? = null
    private var onChangedListener: ((String)->Unit)? = null

    init {
        View.inflate(context, R.layout.view_form_edit_text, this)

        context.obtainStyledAttributes(attrs, R.styleable.FormEditText).use {
            it.getDrawable(R.styleable.FormEditText_formIcon)?.let { drawable -> setIcon(drawable) }
            it.getString(R.styleable.FormEditText_formTitle)?.let { title -> setTitle(title) }
            it.getInteger(R.styleable.FormEditText_android_inputType, InputType.TYPE_CLASS_TEXT)
            setIconEnabled(it.getBoolean(R.styleable.FormEditText_formIconEnabled, true))
        }
        formText.doOnTextChanged { _, _, _, _ -> onChangedListener?.invoke(formText.text?.toString() ?: "") }

    }

    fun setOnTextChanged(block: ((String)->Unit)?) {
        onChangedListener = block
    }

    fun setInputType(type: Int) {
        formText.inputType = type
    }

    fun setIconEnabled(enabled: Boolean) {
        formIcon.isVisible = enabled
    }

    fun setIcon(@DrawableRes res: Int) {
        formIcon.setImageResource(res)
    }

    fun setIcon(drawable: Drawable) {
        formIcon.setImageDrawable(drawable)
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

    fun setRequired(required: Boolean) {
        if (required) {
            formTextContainer.helperText = context.getString(R.string.required)
        } else {
            formTextContainer.helperText = null
        }
    }

    fun getText(): String = formText.text?.toString() ?: ""

    fun setText(value: String) {
        formText.setText(value)
    }

    fun clear() {
        formTextContainer.setText("")
        setErrorTextEnabled(false)
    }

    fun setTitle(@StringRes res: Int) = setTitle(context.getText(res))
    fun setErrorText(@StringRes res: Int) = setErrorText(context.getText(res))

    fun textChangeFlow(): Flow<CharSequence> {
        return formText.textChanges()
    }
}