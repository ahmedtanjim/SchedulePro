@file:Suppress("MemberVisibilityCanBePrivate", "unused", "RemoveExplicitTypeArguments")
package com.shiftboard.schedulepro.ui.form

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shiftboard.schedulepro.ui.R


class FormPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    BaseFormView(context, attrs, defStyleAttr) {

    override val formIcon: ImageView by lazy { findViewById<ImageView>(R.id.form_icon) }
    override val formText: TextInputEditText by lazy { findViewById<TextInputEditText>(R.id.text_edit) }
    override val formTextContainer: TextInputLayout by lazy { findViewById<TextInputLayout>(R.id.text_content) }

    private val endIconView: Drawable by lazy { AppCompatResources.getDrawable(context, R.drawable.close_icon)!! }

    private var pickerDelegate: PickerDelegate<*>? = null
    private var canTextClear: ((String)->Boolean) = {true}

    private val iconInAnim: AnimatorSet by lazy {
        AnimatorSet().apply {
            playTogether(getScaleAnimator(), getAlphaAnimator(0f, 1f))
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    formTextContainer.isEndIconVisible = true
                }
            })
        }
    }

    private val iconOutAnim: ValueAnimator by lazy {
        getAlphaAnimator(1f, 0f).apply {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    formTextContainer.isEndIconVisible = false
                }
            })
        }
    }

    private val clearTextEndIconTextWatcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (formTextContainer.suffixText != null) {
                return
            }
            animateIcon(canTextClear(s?.toString() ?: "") && hasText(s))
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }

    private val onFocusListener = OnFocusChangeListener { v, hasFocus ->
        val hasText = !((v as? EditText)?.text?.isEmpty() ?: true)
        animateIcon(canTextClear((v as? EditText)?.text?.toString() ?: "") && hasText && hasFocus)
    }

    private val clearTextOnEditTextAttachedListener = object: TextInputLayout.OnEditTextAttachedListener {
        override fun onEditTextAttached(textInputLayout: TextInputLayout) {
            val editText = textInputLayout.editText ?: return
            textInputLayout.isEndIconVisible = hasText(editText.text)

            textInputLayout.isEndIconCheckable = false
            editText.onFocusChangeListener = onFocusListener
            editText.removeTextChangedListener(clearTextEndIconTextWatcher)
            editText.addTextChangedListener(clearTextEndIconTextWatcher)
        }
    }

    private val endIconChangedListener = TextInputLayout.OnEndIconChangedListener { textInputLayout, previousIcon ->
        val editText = textInputLayout.editText ?: return@OnEndIconChangedListener
        if (previousIcon == TextInputLayout.END_ICON_CLEAR_TEXT) {
            // Remove any listeners set on the edit text.
            editText.removeTextChangedListener(clearTextEndIconTextWatcher)
            if (editText.onFocusChangeListener == onFocusChangeListener) {
                editText.onFocusChangeListener = null
            }
        }
    }

    init {
        View.inflate(context, R.layout.view_form_picker, this)

        context.obtainStyledAttributes(attrs, R.styleable.FormPicker).use {
            it.getDrawable(R.styleable.FormPicker_formIcon)?.let { drawable -> setIcon(drawable) }
            setTitle(it.getString(R.styleable.FormPicker_formTitle))
            setIconEnabled(it.getBoolean(R.styleable.FormPicker_formIconEnabled, true))
        }

        formText.setOnClickListener {
            pickerDelegate?.invokePicker(this)
        }
        initDelegate()
    }

    // Could make onTextCleared public set, but the DX for this is better.
    fun setPickerDelegate(delegate: PickerDelegate<*>?) {
        pickerDelegate = delegate
    }

    // Could make canTextClear public set, but the DX for this is better.
    fun setShowTextClearListener(block: (String) -> Boolean) {
        canTextClear = block
    }

    @Deprecated("Use setPickerDelegate", level = DeprecationLevel.ERROR)
    override fun setOnClickListener(l: OnClickListener?) {

    }

    override fun clear() {
        pickerDelegate?.onResults(this, null)
        super.clear()
    }

    companion object {
        private const val ANIMATION_FADE_DURATION: Long = 100
        private const val ANIMATION_SCALE_DURATION: Long = 150
        private const val ANIMATION_SCALE_FROM_VALUE = 0.8f

        private fun hasText(editable: Editable?): Boolean = editable?.isNotEmpty() ?: false
    }

    private fun animateIcon(show: Boolean) {
        val shouldSkipAnimation = formTextContainer.isEndIconVisible == show

        if (show) {
            iconOutAnim.cancel()
            iconInAnim.start()
            if (shouldSkipAnimation) {
                iconInAnim.end()
            }
        } else {
            iconInAnim.cancel()
            iconOutAnim.start()
            if (shouldSkipAnimation) {
                iconOutAnim.end()
            }
        }
    }

    private fun initDelegate() {
        formTextContainer.endIconDrawable = endIconView
        formTextContainer.endIconContentDescription = formTextContainer.resources.getText(R.string.clear_text_end_icon_content_description)
        formTextContainer.setEndIconOnClickListener {
            formTextContainer.editText?.text?.clear()
            pickerDelegate?.onResults(this, null)
        }

        formTextContainer.addOnEditTextAttachedListener(clearTextOnEditTextAttachedListener)
        formTextContainer.addOnEndIconChangedListener(endIconChangedListener)
    }

    private fun getAlphaAnimator(vararg values: Float): ValueAnimator {
        return ValueAnimator.ofFloat(*values).apply {
            interpolator = AnimationUtils.LINEAR_INTERPOLATOR
            duration = ANIMATION_FADE_DURATION
            addUpdateListener {
                endIconView.alpha = (255 * ((it.animatedValue as? Float) ?: 0f)).toInt()
            }
        }
    }

    private fun getScaleAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(ANIMATION_SCALE_FROM_VALUE, 1f).apply {
            interpolator = AnimationUtils.LINEAR_INTERPOLATOR
            duration = ANIMATION_FADE_DURATION
            addUpdateListener {
//                endIconView.
//                endIconView. = (255 * ((it.animatedValue as? Float) ?: 0f)).toInt()
            }
        }
    }
}

abstract class PickerDelegate<T> {
    abstract fun createPicker(view: FormPicker, pickerResults: (T?)->Unit)
    abstract fun onResults(view: FormPicker, result: T?)

    fun invokePicker(view: FormPicker) {
        createPicker(view) { onResults(view, it) }
    }
}
