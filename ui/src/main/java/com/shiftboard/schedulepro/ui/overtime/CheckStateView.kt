package com.shiftboard.schedulepro.ui.overtime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.shiftboard.schedulepro.ui.R
import com.shiftboard.schedulepro.ui.dp

open class CheckStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {

    private var iconWidth: Float = 0f
    private var radius: Float = 0f
    var state: State = State.INACTIVE
        set(value) {
            field = value
            invalidate()
        }

    private val enabledColor by lazy {
        ContextCompat.getColor(context, R.color.white)
    }

    private val disabledColor by lazy {
        ContextCompat.getColor(context, R.color.very_light_gray)
    }

    private val inactivePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = enabledColor
        }
    }

    private val activePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.colorPrimary)

        }
    }

    private val inactiveBorderPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.schedule_label)
        }
    }

    private val checkMarkDrawable by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_check) as VectorDrawable
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        when (state) {
            State.INACTIVE -> canvas?.drawInactive(false)
            State.MULTISELECT_INACTIVE -> canvas?.drawInactive(true)
            else -> canvas?.drawActive()
        }
    }

    private fun Canvas.drawInactive(isMultiselect: Boolean) {
        drawCircle(width / 2f, height / 2f, radius, inactivePaint)
        drawCircle(width / 2f, height / 2f, radius, inactiveBorderPaint)

        if (isMultiselect) {
            checkMarkDrawable.setTint(disabledColor)

            val widthPadding = ((width - iconWidth) / 2f).toInt()
            val heightPadding = ((height - iconWidth) / 2f).toInt()

            checkMarkDrawable.setBounds(widthPadding, heightPadding,
                width - widthPadding, height - heightPadding)
            checkMarkDrawable.draw(this)
        }
    }

    private fun Canvas.drawActive() {
        drawCircle(width / 2f, height / 2f, radius, activePaint)

        checkMarkDrawable.setTint(enabledColor)

        val widthPadding = ((width - iconWidth) / 2f).toInt()
        val heightPadding = ((height - iconWidth) / 2f).toInt()

        checkMarkDrawable.setBounds(widthPadding, heightPadding,
            width - widthPadding,
            height - heightPadding)

        checkMarkDrawable.draw(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originalWidth = MeasureSpec.getSize(widthMeasureSpec)

        radius = (originalWidth * viewPadding) / 2f
        iconWidth = originalWidth * iconPadding

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY)
        )
    }

    enum class State {
        ACTIVE, INACTIVE,
        MULTISELECT_ACTIVE, MULTISELECT_INACTIVE,
    }

    companion object {
        // These make the assumption that the view is 48 dp
        private var viewPadding = .8f
        private var iconPadding = .5f
    }
}