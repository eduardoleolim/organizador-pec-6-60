package org.eduardoleolim.organizadorPec660.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorPec660.app.main.router.MainProvider
import org.eduardoleolim.organizadorPec660.core.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorPec660.core.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandlerExecutionError

class AuthScreenModel(private val navigator: Navigator, private val queryBus: QueryBus) : ScreenModel {
    fun login(username: String, password: String, callback: (Result<AuthUserResponse>) -> Unit) {
        screenModelScope.launch(Dispatchers.IO) {
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
