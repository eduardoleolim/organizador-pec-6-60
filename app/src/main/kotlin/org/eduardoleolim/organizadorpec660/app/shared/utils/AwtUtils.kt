package org.eduardoleolim.organizadorpec660.app.shared.utils

import androidx.compose.ui.graphics.Color

fun Color.toAwt(
    red: Float = this.red,
    green: Float = this.green,
    blue: Float = this.blue,
    alpha: Float = this.alpha
): java.awt.Color {
    return java.awt.Color(red, green, blue, alpha)
}
