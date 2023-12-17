package org.eduardoleolim.organizadorpec660.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.main.router.MainProvider
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandlerExecutionError

class AuthScreenModel(private val navigator: Navigator, private val queryBus: QueryBus) : ScreenModel {
    fun login(username: String, password: String): Result<AuthUserResponse> {
        if (username.isEmpty() || password.isEmpty()) {
            return Result.failure(InvalidCredentialsException(username.isEmpty(), password.isEmpty()))
        }

        try {
            queryBus.ask<AuthUserResponse>(AuthenticateUserQuery(username, password))
                .let {
                    return Result.success(it)
                }
        } catch (e: QueryHandlerExecutionError) {
            return Result.failure(e.cause!!)
        }
    }

    fun navigateToHome(user: AuthUserResponse) {
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
