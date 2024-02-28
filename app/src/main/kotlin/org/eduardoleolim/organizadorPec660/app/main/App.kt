package org.eduardoleolim.organizadorPec660.app.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
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
import org.eduardoleolim.organizadorPec660.app.main.customWindow.*
import org.eduardoleolim.organizadorPec660.app.main.router.Router
import org.eduardoleolim.organizadorPec660.app.shared.theme.DarkColors
import org.eduardoleolim.organizadorPec660.app.shared.theme.LightColors
import org.eduardoleolim.organizadorPec660.app.shared.utils.isSystemInDarkTheme
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

enum class SystemTheme {
    DARK, LIGHT, DEFAULT
}

class App(private val commandBus: CommandBus, private val queryBus: QueryBus) {
    fun start() = application {

        MainWindow(
            onCloseRequest = { exitApplication() }
        )
    }

    @Composable
    private fun MainWindow(onCloseRequest: () -> Unit) {
        val state = rememberWindowState()
        var selectedTheme by remember { mutableStateOf(SystemTheme.DEFAULT) }
        val isSystemInDarkTheme = isSystemInDarkTheme()

        MaterialTheme(
            colorScheme = when (selectedTheme) {
                SystemTheme.DARK -> DarkColors
                SystemTheme.LIGHT -> LightColors
                SystemTheme.DEFAULT -> if (isSystemInDarkTheme) DarkColors else LightColors
            }
        ) {
            val icon = painterResource("assets/icon.png")
            CustomWindow(
                state = state,
                onCloseRequest = { onCloseRequest() },
                defaultTitle = "Organizador PEC-6-60",
                defaultIcon = icon
            ) {
                WindowIcon(icon) {}
                WindowCenter {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp),
                    ) {
                        Icon(
                            imageVector = when (selectedTheme) {
                                SystemTheme.DARK -> Icons.Filled.DarkMode
                                SystemTheme.LIGHT -> Icons.Filled.LightMode
                                SystemTheme.DEFAULT -> Icons.Filled.Contrast
                            },
                            contentDescription = "Toggle theme",
                            modifier = Modifier
                                .windowFrameItem("theme", HitSpots.OTHER_HIT_SPOT)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedTheme = when (selectedTheme) {
                                        SystemTheme.DARK -> SystemTheme.LIGHT
                                        SystemTheme.LIGHT -> SystemTheme.DEFAULT
                                        SystemTheme.DEFAULT -> SystemTheme.DARK
                                    }
                                }
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                }

                Router(commandBus, queryBus)
            }
        }
    }
}
