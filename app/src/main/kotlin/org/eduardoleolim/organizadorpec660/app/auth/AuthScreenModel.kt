package org.eduardoleolim.organizadorpec660.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.main.router.MainProvider
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

class AuthScreenModel(
    private val navigator: Navigator,
    private val commandBus: CommandBus,
    private val queryBus: QueryBus
) : ScreenModel {
    fun login(username: String, password: String) {
        println("Login: $username, $password")

        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen))
    }
}
