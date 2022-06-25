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


class CheckableToggleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : CheckStateView(context, attrs, defStyle) {

    var onToggleListener: ToggleListener? = null

    init {
        setOnClickListener {
            toggleState()
        }
    }

    fun toggleState() {
        state = when (state) {
            State.ACTIVE -> State.INACTIVE
            State.INACTIVE -> State.ACTIVE
            State.MULTISELECT_ACTIVE -> State.MULTISELECT_INACTIVE
            State.MULTISELECT_INACTIVE -> State.MULTISELECT_ACTIVE
        }
        onToggleListener?.onToggle(isActiveState(state), this)
        invalidate()
    }

    private fun isActiveState(state: State): Boolean {
        return when (state) {
            State.ACTIVE, State.MULTISELECT_ACTIVE -> true
            else -> false
        }
    }

    fun interface ToggleListener {
        fun onToggle(active: Boolean, view: View)
    }
}