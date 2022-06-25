package com.shiftboard.schedulepro.ui

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.minus
import com.shiftboard.schedulepro.core.common.utils.parseAsColor


class ShiftListElementBackground(
    val paintColor: Int,
    val projected: Boolean,
    val leave: Boolean = false,
    val open: Boolean = false
) : Drawable() {
    private val borderPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            if (projected) {
                strokeWidth = STROKE_WIDTH * 2
                pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
            } else {
                strokeWidth = STROKE_WIDTH
            }
            color = paintColor
        }
    }

    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = when {
                leave -> parseAsColor("#6B7279")
                projected -> parseAsColor("#f0efef")
                else -> ColorUtils.setAlphaComponent(paintColor, 0x1A /* 10% Alpha */)
            }
        }
    }

    private val colorBarPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = paintColor
        }
    }

    override fun draw(canvas: Canvas) {
        val boundF = RectF(bounds)
        //
        val strokeOffset = STROKE_WIDTH / 2

        canvas.save()
        canvas.clipRect(12.dp, bounds.top, bounds.right, bounds.bottom)

        canvas.drawRoundRect(
            bounds.left + strokeOffset, bounds.top + strokeOffset, bounds.right - strokeOffset, bounds.bottom - strokeOffset,
            CORNER_RADIUS, CORNER_RADIUS, backgroundPaint)
        if (!open) {
            canvas.drawRoundRect(
                bounds.left + strokeOffset,
                bounds.top + strokeOffset,
                bounds.right - strokeOffset,
                bounds.bottom - strokeOffset,
                CORNER_RADIUS,
                CORNER_RADIUS,
                borderPaint)
        }

        canvas.restore()
        canvas.save()
        canvas.clipRect(bounds.left, bounds.top, 12.dp, bounds.bottom)

        canvas.drawRoundRect(boundF, CORNER_RADIUS, CORNER_RADIUS, colorBarPaint)

        canvas.restore()
    }

    override fun setAlpha(i: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    companion object {
        private val STROKE_WIDTH = 2.dp.toFloat()
        private val CORNER_RADIUS = 4.dp.toFloat()
    }
}