package org.eduardoleolim.organizadorpec660.shared.notification

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.window.TrayState

internal val LocalTrayState = compositionLocalOf<TrayState> { error("tray state not provided") }