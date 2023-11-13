package org.eduardoleolim.organizadorpec660.app.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import java.awt.*
import javax.swing.JFrame

@Composable
fun MainWindow(
    title: String = "Untitled",
    onCloseRequest: () -> Unit,
    resizable: Boolean = true,
    minHeight: Int = 0,
    minWidth: Int = 0,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit
) {
    val windowState by remember { mutableStateOf(WindowState()) }
    var surfaceModifier by remember { mutableStateOf(Modifier.fillMaxSize()) }

    Window(
        onCloseRequest = onCloseRequest,
        state = windowState,
        title = title,
        resizable = resizable,
        undecorated = true,
        transparent = true
    ) {
        window.minimumSize = Dimension(minWidth, minHeight)

        when (windowState.placement) {
            WindowPlacement.Floating -> {
                window.isResizable = resizable
                surfaceModifier = Modifier.fillMaxSize().padding(5.dp).shadow(3.dp, RoundedCornerShape(10.dp))
            }

            WindowPlacement.Maximized -> {
                window.isResizable = false
                surfaceModifier = Modifier.fillMaxSize().padding(0.dp)

                val graphicConfiguration =
                    currentGraphicDevice(window)?.defaultConfiguration ?: window.graphicsConfiguration
                val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicConfiguration)

                window.maximizedBounds = graphicConfiguration?.let {
                    Rectangle(
                        it.bounds.x + screenInsets.left,
                        it.bounds.y + screenInsets.top,
                        it.bounds.width - screenInsets.left - screenInsets.right,
                        it.bounds.height - screenInsets.top - screenInsets.bottom
                    )
                }
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
                modifier = surfaceModifier
            ) {
                content()
            }
        }
    }
}

fun currentGraphicDevice(frame: JFrame): GraphicsDevice? {
    val centerPoint = Point(frame.location.x + frame.width / 2, frame.location.y + frame.height / 2)

    return GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices.firstOrNull {
        it.defaultConfiguration.bounds.contains(centerPoint)
    }
}
