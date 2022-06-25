package com.shiftboard.schedulepro.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.shiftboard.schedulepro.core.common.utils.colorAlpha

class HourSummaryBackground(@ColorInt backgroundColor: Int) : Drawable() {
    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = colorAlpha(backgroundColor, .1f)
        }
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(0f, 0f, bounds.right.toFloat(), bounds.bottom.toFloat(), 8.dp.toFloat(),
            8.dp.toFloat(), backgroundPaint)
    }

    override fun setAlpha(i: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}