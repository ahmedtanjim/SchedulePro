package com.shiftboard.schedulepro.content.trade

fun safeParseColor(colorString: String?): Int {
    if (colorString.isNullOrEmpty()) return android.graphics.Color.WHITE
    return try {
        android.graphics.Color.parseColor(colorString)
    }
    catch (e: Error) {
        android.graphics.Color.WHITE
    }
}