package org.eduardoleolim.organizadorpec660.app.main

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.application
import org.eduardoleolim.organizadorpec660.app.main.router.Router
import org.eduardoleolim.organizadorpec660.app.main.window.MainWindow
import org.eduardoleolim.organizadorpec660.app.main.window.TitleBar
import org.eduardoleolim.organizadorpec660.app.shared.theme.DarkColors
import org.eduardoleolim.organizadorpec660.app.shared.theme.LightColors
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

class App(private val commandBus: CommandBus, private val queryBus: QueryBus) {
    fun start() = application {
        MainWindow(
            title = "Organizador PEC-6-60",
            icon = painterResource("assets/icon.ico"),
            onCloseRequest = ::exitApplication,
            isDarkTheme = false,
            lightColorScheme = LightColors,
            darkColorScheme = DarkColors,
            minHeight = 600,
            minWidth = 800,
        ) {
            Column {
                TitleBar(window, ::exitApplication)
                Router(window, commandBus, queryBus)
            }
        }
    }
}
