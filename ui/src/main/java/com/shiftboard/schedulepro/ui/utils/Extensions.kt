package com.shiftboard.schedulepro.ui.utils

import androidx.recyclerview.widget.RecyclerView


// Its surprising how often I have to turn off these animations
fun RecyclerView.disableAnimations() {
    itemAnimator?.changeDuration = 0
    itemAnimator?.addDuration = 0
    itemAnimator?.removeDuration = 0
    itemAnimator?.moveDuration = 0
}