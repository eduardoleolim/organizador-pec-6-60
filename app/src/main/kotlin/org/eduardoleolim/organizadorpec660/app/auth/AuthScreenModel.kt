package org.eduardoleolim.organizadorpec660.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.main.router.MainProvider
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.core.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandlerExecutionError

class AuthScreenModel(private val navigator: Navigator, private val queryBus: QueryBus) : ScreenModel {
    fun login(username: String, password: String, callback: (Result<AuthUserResponse>) -> Unit) {
        screenModelScope.launch {
            val isUsernameInvalid = username.isEmpty()
            val isPasswordInvalid = password.isEmpty()

            if (isUsernameInvalid || isPasswordInvalid) {
                callback(Result.failure(InvalidCredentialsException(isUsernameInvalid, isPasswordInvalid)))
            } else {
                try {
                    callback(Result.success(queryBus.ask(AuthenticateUserQuery(username, password))))
                } catch (e: QueryHandlerExecutionError) {
                    callback(Result.failure(e.cause!!))
                }
            }
        }
    }

    fun navigateToHome(user: AuthUserResponse) {
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
