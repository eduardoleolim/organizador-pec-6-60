package org.eduardoleolim.organizadorpec660.app.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jthemedetecor.OsThemeDetector
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.app.main.customWindow.*
import org.eduardoleolim.organizadorpec660.app.main.router.Router
import org.eduardoleolim.organizadorpec660.app.shared.theme.DarkColors
import org.eduardoleolim.organizadorpec660.app.shared.theme.LightColors
import java.util.function.Consumer
import javax.swing.SwingUtilities

class App(private val commandBus: CommandBus, private val queryBus: QueryBus) {
    fun start() = application {
        val state = rememberWindowState()
        var isThemeSelected by remember { mutableStateOf(false) }
        var isDarkTheme by remember { mutableStateOf(true) }

        DisposableEffect(Unit) {
            val osThemeDetector = OsThemeDetector.getDetector()

            val consumer = Consumer<Boolean> {
                SwingUtilities.invokeLater {
                    if (!isThemeSelected) {
                        isDarkTheme = it
                        isThemeSelected = true
                    }
                }
            }

            osThemeDetector.registerListener(consumer)
            onDispose {
                osThemeDetector.removeListener(consumer)
            }
        }

        MaterialTheme(
            colorScheme = if (isDarkTheme) DarkColors else LightColors
        ) {
            val icon = painterResource("assets/icon.png")
            CustomWindow(
                state = state,
                onCloseRequest = { exitApplication() },
                defaultTitle = "Organizador PEC-6-60",
                defaultIcon = icon
            ) {
                WindowIcon(icon) {}
                WindowCenter {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp),
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Toggle theme",
                            modifier = Modifier
                                .windowFrameItem("theme", HitSpots.OTHER_HIT_SPOT)
                                .clickable { isDarkTheme = !isDarkTheme }
                                .padding(4.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Router(commandBus, queryBus)
            }
        }
    }
}
