package org.eduardoleolim.organizadorpec660.shared.window.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun fromWidth(width: Dp): WindowSize {
            return when {
                width < 600.dp -> COMPACT
                width < 840.dp -> MEDIUM
                else -> EXPANDED
            }
        }
    }
}


