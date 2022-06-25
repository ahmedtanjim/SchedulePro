package com.shiftboard.schedulepro.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class LeaveRequestHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
): LinearLayout(context, attrs, defStyle)  {

    private val requestType: TextView

    private val backgroundPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    var color: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    private val backgroundColor by lazy {
        ContextCompat.getColor(context, R.color.approved_leave)
    }

    fun setText(text: String) {
        requestType.text = text
    }

    init {
        View.inflate(context, R.layout.leave_request_header, this)

        requestType = findViewById(R.id.request_type)

        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {

        backgroundPaint.color = backgroundColor
        canvas.drawRect(0f, requestType.top.toFloat(), width.toFloat(), requestType.bottom.toFloat(), backgroundPaint)

        backgroundPaint.color = color
        canvas.drawRect(0f, requestType.bottom.toFloat(), width.toFloat(), height.toFloat(), backgroundPaint)

        super.onDraw(canvas)
    }
}