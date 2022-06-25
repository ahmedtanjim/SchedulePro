package com.shiftboard.schedulepro.content.profile.unavailability

import android.view.ViewTreeObserver
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.InputMode.Companion.Keyboard
import androidx.compose.ui.platform.LocalView

enum class KeyBoard {
    Opened, Closed
}

@Composable
fun keyBoardAsState(): State<KeyBoard> {
    val keyBoardState = remember {
        mutableStateOf(KeyBoard.Closed)
    }
    val view = LocalView.current
    DisposableEffect(key1 = view){
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyBoardState.value = if (keypadHeight > screenHeight * 0.15) {
                KeyBoard.Opened
            } else {
                KeyBoard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }
    return keyBoardState
}

fun Int.toBinaryArray(): Array<Boolean> {
    return String
        .format("%" + 7 + "s", this.toString(2))
        .replace(" ".toRegex(),"0")
        .toCharArray()
        .map {
            it == '1'
        }.toTypedArray()
}