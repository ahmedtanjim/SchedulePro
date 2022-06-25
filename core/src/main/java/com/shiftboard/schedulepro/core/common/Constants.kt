package com.shiftboard.schedulepro.core.common

import android.widget.ViewFlipper

// There isn't anything that forces this to be true this is just a convention that is being followed
// on most pages and is easier to read than magic numbers
enum class PageState {
    LOADING,
    ERROR,
    SUCCESS,
}

/**
 * Only use this function if you are following the three state convention and are using the same
 * state mapping used for page state otherwise use a different function.
 */
fun ViewFlipper.setState(state: PageState) {
    displayedChild = state.ordinal
}

