package com.shiftboard.schedulepro.ui.overtime

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.shiftboard.schedulepro.core.common.utils.parseAsColor
import com.shiftboard.schedulepro.ui.R
import com.shiftboard.schedulepro.ui.dp
import com.shiftboard.schedulepro.ui.height
import kotlin.math.floor

class ColorBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {

    private var maxCount = 8
    private var overtimeElements: List<Int> = listOf()
    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }
    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textSize = 16.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.black)
            textAlign = Paint.Align.LEFT
        }
    }

    init {
        // Fill the buffer with some random colors if we are in edit mode.  Normally this won't happen.
        if (isInEditMode) {
            overtimeElements = randomColorCycle + randomColorCycle + randomColorCycle + randomColorCycle
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val originalWidth = MeasureSpec.getSize(widthMeasureSpec)

        maxCount =
            floor((originalWidth - BAR_PADDING).toFloat() / (BAR_WIDTH + BAR_PADDING).toFloat()).toInt()

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        overtimeElements.forEachIndexed loop@{ index, color ->
            if (index == maxCount - 1 && overtimeElements.size != maxCount) {
                canvas.drawText("+", (BAR_PADDING + (BAR_PADDING * index) + (BAR_WIDTH * index)).toFloat(), height / 2f - textPaint.height / 2f, textPaint)
                return
            } else {
                backgroundPaint.color = color
                canvas.drawRoundRect(
                    (BAR_PADDING + (BAR_PADDING * index) + (BAR_WIDTH * index)).toFloat(),
                    top + BAR_TOP_PADDING,
                    (BAR_PADDING + (BAR_PADDING * index) + BAR_WIDTH + (BAR_WIDTH * index)).toFloat(),
                    bottom - BAR_BOTTOM_PADDING,
                    BAR_RADIUS, BAR_RADIUS,
                    backgroundPaint
                )
            }
        }
    }

    fun setColorTags(colors: List<Int>) {
        overtimeElements = colors
        invalidate()
    }

    companion object {
        private val BAR_PADDING = 8.dp
        private val BAR_TOP_PADDING = 8.dp.toFloat()
        private val BAR_BOTTOM_PADDING = 8.dp.toFloat()
        private val BAR_RADIUS = 3.dp.toFloat()
        private val BAR_WIDTH = 10.dp

        // Only gets initialized if we are in edit mode
        private val randomColorCycle by lazy {
            listOf(
                parseAsColor("#009D00"),
                parseAsColor("#6449B1"),
                parseAsColor("#1359AB"),
                parseAsColor("#006163"),
                parseAsColor("#FFC000"),
                parseAsColor("#455A64"),
                parseAsColor("#7C0D12"),
            )
        }
    }
}