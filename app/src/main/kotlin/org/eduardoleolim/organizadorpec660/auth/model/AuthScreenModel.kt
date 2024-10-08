package org.eduardoleolim.organizadorpec660.auth.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandlerExecutionError
import org.eduardoleolim.organizadorpec660.shared.router.MainProvider
import org.eduardoleolim.organizadorpec660.shared.utils.AppConfig

class AuthScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    val appVersion = AppConfig.version

    var credentials by mutableStateOf(Credentials())
        private set

    fun updateUsername(newUsername: String) {
        credentials = credentials.copy(username = newUsername)
    }

    fun updatePassword(newPassword: String) {
        credentials = credentials.copy(password = newPassword)
    }

    fun login() {
        val (username, password) = credentials
        screenModelScope.launch(dispatcher) {
            authState = AuthState.InProgress
            delay(500)

            val isUsernameEmpty = username.isEmpty()
            val isPasswordEmpty = password.isEmpty()

            if (isUsernameEmpty || isPasswordEmpty) {
                authState = AuthState.Error(InvalidCredentialsException(isUsernameEmpty, isPasswordEmpty))
                return@launch
            }

            try {
                val authUserResponse: AuthUserResponse = queryBus.ask(AuthenticateUserQuery(username, password))
                authState = AuthState.Success(authUserResponse)
            } catch (e: QueryHandlerExecutionError) {
                authState = AuthState.Error(e.cause!!)
            }
        }
    }

    fun navigateToHomeScreen(user: AuthUserResponse) {
        authState = AuthState.Idle
        credentials = Credentials()
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
