package org.eduardoleolim.organizadorpec660.app.main.window

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import java.awt.*
import javax.swing.JFrame

@Composable
fun MainWindow(
    title: String = "Untitled",
    icon: Painter? = null,
    onCloseRequest: () -> Unit = {},
    resizable: Boolean = true,
    minHeight: Int = 0,
    minWidth: Int = 0,
    isDarkTheme: Boolean? = null,
    lightColorScheme: ColorScheme = MaterialTheme.colorScheme,
    darkColorScheme: ColorScheme = MaterialTheme.colorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable (FrameWindowScope.() -> Unit)
) {
    val windowState = rememberWindowState()
    var surfaceModifier by remember { mutableStateOf(Modifier.fillMaxSize()) }

    Window(
        onCloseRequest = onCloseRequest,
        state = windowState,
        title = title,
        icon = icon,
        resizable = resizable,
        undecorated = true,
        transparent = true
    ) {
        val colorScheme = when (isDarkTheme ?: isSystemInDarkTheme()) {
            true -> darkColorScheme
            false -> lightColorScheme
        }

        window.apply {
            minimumSize = Dimension(minWidth, minHeight)
            maximizedBounds = calculateWorkArea(window)
        }

        when (windowState.placement) {
            WindowPlacement.Floating -> {
                window.isResizable = resizable
                surfaceModifier = Modifier.fillMaxSize().padding(5.dp).shadow(3.dp, RoundedCornerShape(10.dp))
            }

            WindowPlacement.Maximized -> {
                window.isResizable = false
                surfaceModifier = Modifier.fillMaxSize().padding(0.dp)
            }

            WindowPlacement.Fullscreen -> {
                window.isResizable = resizable
                surfaceModifier = Modifier.fillMaxSize().padding(0.dp)
            }
        }

        MaterialTheme(
            colorScheme = colorScheme,
            shapes = shapes,
            typography = typography
        ) {
            Surface(
                modifier = surfaceModifier,
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                content()
            }
        }
    }
}

private fun calculateWorkArea(frame: JFrame): Rectangle {
    val graphicConfiguration = currentGraphicDevice(frame)?.defaultConfiguration ?: frame.graphicsConfiguration
    val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicConfiguration)

    return graphicConfiguration.let {
        Rectangle(
            it.bounds.x + screenInsets.left,
            it.bounds.y + screenInsets.top,
            it.bounds.width - screenInsets.left - screenInsets.right,
            it.bounds.height - screenInsets.top - screenInsets.bottom
        )
    }
}

private fun currentGraphicDevice(frame: JFrame): GraphicsDevice? {
    val centerPoint = Point(frame.location.x + frame.width / 2, frame.location.y + frame.height / 2)

    return GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.firstOrNull {
        it.defaultConfiguration.bounds.contains(centerPoint)
    }
}
