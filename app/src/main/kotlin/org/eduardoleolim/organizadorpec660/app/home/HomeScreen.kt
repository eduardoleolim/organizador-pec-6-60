package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.awt.ComposeWindow
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

class HomeScreen(
    private val window: ComposeWindow,
    private val user: AuthUserResponse,
    private val commandBus: CommandBus,
    private val queryBus: QueryBus
) : Screen {
    @Composable
    override fun Content() {
        window.isResizable = true
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HomeScreenModel(navigator, commandBus, queryBus) }

        Button(onClick = screenModel::logout) {
            Text("Click me")
        }
    }
}
