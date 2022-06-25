package com.shiftboard.schedulepro.ui.schedule

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.shiftboard.schedulepro.ui.R
import com.shiftboard.schedulepro.ui.dp

class SchedulePalette(context: Context) {
    val headerText by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.SANS_SERIF
            textSize = 24.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.text_dark)
            textAlign = Paint.Align.LEFT
        }
    }

    val labelText by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textSize = 16.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.schedule_label)
            textAlign = Paint.Align.CENTER
        }
    }

    val dateLabel by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.SANS_SERIF
            textSize = 16.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.black)
            textAlign = Paint.Align.CENTER
        }
    }

    val todayLabel by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textSize = 16.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            textAlign = Paint.Align.CENTER
        }
    }

    val selectedLabel by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            textSize = 16.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.white)
            textAlign = Paint.Align.CENTER
        }
    }


    val iconPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    val projectedIconPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.dp.toFloat()
        }
    }

    val signupPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    val iconPaintLC by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context, R.color.bluish_gray)
            strokeWidth = .5f.dp.toFloat()
        }
    }

    val outerCirclePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.white)
        }
    }

    val innerCirclePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.alert_red)
        }
    }

    val plusPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.dp.toFloat()
            color = ContextCompat.getColor(context, R.color.text_dark)
        }
    }


    val todayHighlight by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimaryLight)
            style = Paint.Style.FILL
        }
    }

    val selectedHighlight by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            style = Paint.Style.FILL
        }
    }
}