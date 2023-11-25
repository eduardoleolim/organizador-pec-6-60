package org.eduardoleolim.organizadorpec660.app.main.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.auth.AuthScreen
import org.eduardoleolim.organizadorpec660.app.home.HomeScreen
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

@Composable
fun Router(window: ComposeWindow, commandBus: CommandBus, queryBus: QueryBus) {
    ScreenRegistry {
        register<MainProvider.AuthScreen> {
            AuthScreen(window, commandBus, queryBus)
        }
        register<MainProvider.HomeScreen> {
            HomeScreen(window, it.user, commandBus, queryBus)
        }
    }

    Navigator(ScreenRegistry.get(MainProvider.AuthScreen))
}
