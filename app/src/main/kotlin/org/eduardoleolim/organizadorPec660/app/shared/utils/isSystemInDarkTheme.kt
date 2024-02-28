package org.eduardoleolim.organizadorPec660.app.shared.utils

import androidx.compose.runtime.*
import com.jthemedetecor.OsThemeDetector
import java.util.function.Consumer
import javax.swing.SwingUtilities

@Composable
fun isSystemInDarkTheme(): Boolean {
    var isDarkTheme by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val osThemeDetector = OsThemeDetector.getDetector()
        isDarkTheme = osThemeDetector.isDark

        val consumer = Consumer<Boolean> {
            SwingUtilities.invokeLater {
                isDarkTheme = it
            }
        }

        osThemeDetector.registerListener(consumer)
        onDispose {
            osThemeDetector.removeListener(consumer)
        }
    }

    return isDarkTheme
}
