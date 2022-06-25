package com.shiftboard.schedulepro.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.graphics.ColorUtils
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.ui.DetailsHeader.OnNavigationClicked


class DetailsHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(context, attrs, defStyle) {

    private val headerTitle: TextView
    private val colorBar: View
    private val backButton: ImageView

    var onNavigationClicked: OnNavigationClicked = OnNavigationClicked { }

    private val borderPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.dp.toFloat()
        }
    }

    private val dashedBorderPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.dp.toFloat()
            pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
        }
    }

    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    var color: Int = Color.WHITE
        set(value) {
            field = value
            colorBar.setBackgroundColor(field)
            invalidate()
        }

    var dashed: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private var overrideColor: Int? = null
    var drawBorder: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    private val statusBarColor: Int

    init {
        View.inflate(context, R.layout.details_header, this)

        headerTitle = findViewById(R.id.header_title)
        colorBar = findViewById(R.id.color_bar)
        backButton = findViewById(R.id.back_button)

        backButton.setOnClickListener {
            onNavigationClicked.onBackPressed(it)
        }

        colorBar.setBackgroundColor(color)
        statusBarColor = getStatusBarColor()

        context.obtainStyledAttributes(attrs, R.styleable.DetailsHeader).use { ta ->
            ta.getColor(R.styleable.DetailsHeader_sft_background_color, 0).takeIf { it != 0 }?.let {
                overrideColor = it
            }
            drawBorder = ta.getBoolean(R.styleable.DetailsHeader_sft_draw_border, true)
            ta.getBoolean(R.styleable.DetailsHeader_sft_light_text, false).let {
                setTextColor(it)
            }
        }

        setWillNotDraw(false)
    }

    private fun setTextColor(useLightText: Boolean) {
        if (useLightText) {
            val light = ContextCompat.getColor(context, R.color.white)

            headerTitle.setTextColor(light)
            backButton.imageTintList = ColorStateList.valueOf(light)
        }
    }

    private fun getStatusBarColor(): Int {
        return getActivity(context)?.window?.statusBarColor ?: run {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
            typedValue.data
        }
    }

    private fun getActivity(context: Context?): Activity? {
        if (context == null) return null
        if (context is Activity) return context
        return if (context is ContextWrapper) getActivity(context.baseContext) else null
    }

    fun setOverrideColor(color: Int, useLightText: Boolean) {
        overrideColor = color
        setTextColor(useLightText)

        invalidate()
    }

    fun setTitleText(text: String) {
        headerTitle.text = text
    }

    override fun draw(canvas: Canvas) {

        val curveRadius = 12.dp.toFloat()
        val strokeOffset = when {
            dashed -> dashedBorderPaint.strokeWidth / 2f
            drawBorder -> borderPaint.strokeWidth / 2f
            else -> 0f
        }

        borderPaint.color = color
        dashedBorderPaint.color = color
        backgroundPaint.color = Color.WHITE

        canvas.save()
        canvas.clipRect(0f, 0f, width.toFloat(), colorBar.bottom.toFloat())
        canvas.drawColor(statusBarColor)

        // use override color or use a 10% alpha of our color bar color
        if (dashed) {
            backgroundPaint.color =
                overrideColor ?: parseAsColor("#f0efef")
        } else {
            backgroundPaint.color =
                overrideColor ?: ColorUtils.setAlphaComponent(color, 0x1A /* 10% Alpha */)
        }
        canvas.drawRoundRect(strokeOffset, strokeOffset, width - strokeOffset,
            colorBar.bottom.toFloat() + curveRadius, curveRadius, curveRadius, backgroundPaint)

        if (drawBorder) {
            canvas.drawRoundRect(strokeOffset,
                strokeOffset,
                width - strokeOffset,
                colorBar.bottom.toFloat() + curveRadius,
                curveRadius,
                curveRadius,
                if (dashed) dashedBorderPaint else borderPaint)
        }

        canvas.restore()

        super.draw(canvas)
    }

    fun interface OnNavigationClicked {
        fun onBackPressed(view: View)
    }
}