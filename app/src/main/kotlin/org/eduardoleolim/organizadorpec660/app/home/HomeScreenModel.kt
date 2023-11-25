package org.eduardoleolim.organizadorpec660.app.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.auth.AuthScreen
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

class HomeScreenModel(
    private val navigator: Navigator,
    private val commandBus: CommandBus,
    private val queryBus: QueryBus
) : ScreenModel {
    fun logout() {
        navigator.popUntil<AuthScreen, Screen>()
    }
}
