package org.eduardoleolim.organizadorpec660.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.main.router.MainProvider
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandlerExecutionError
import kotlin.concurrent.thread

class AuthScreenModel(private val navigator: Navigator, private val queryBus: QueryBus) : ScreenModel {
    fun login(username: String, password: String, callback: (Result<AuthUserResponse>) -> Unit) {
        thread(start = true) {
            val isUsernameInvalid = username.isEmpty()
            val isPasswordInvalid = password.isEmpty()

            val result = if (isUsernameInvalid || isPasswordInvalid) {
                Result.failure(InvalidCredentialsException(isUsernameInvalid, isPasswordInvalid))
            } else {
                try {
                    Result.success(queryBus.ask<AuthUserResponse>(AuthenticateUserQuery(username, password)))
                } catch (e: QueryHandlerExecutionError) {
                    Result.failure(e.cause!!)
                }
            }

            callback(result)
        }
    }

    fun navigateToHome(user: AuthUserResponse) {
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
