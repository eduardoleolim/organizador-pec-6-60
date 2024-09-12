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
import org.eduardoleolim.organizadorpec660.auth.data.InvalidCredentialsException
import org.eduardoleolim.organizadorpec660.shared.router.MainProvider
import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.auth.application.authenticate.AuthenticateUserQuery
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandlerExecutionError

sealed class AuthState {
    data object Idle : AuthState()
    data object InProgress : AuthState()
    data class Success(val user: AuthUserResponse) : AuthState()
    data class Error(val error: Throwable) : AuthState()
}

class AuthScreenModel(
    private val navigator: Navigator,
    private val queryBus: QueryBus,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : ScreenModel {
    var authState by mutableStateOf<AuthState>(AuthState.Idle)
        private set

    fun login(username: String, password: String) {
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

    fun navigateToHomeView(user: AuthUserResponse) {
        authState = AuthState.Idle
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
