package org.eduardoleolim.organizadorPec660.app.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

sealed class AuthState {
    data object Idle : AuthState()
    data object InProgress : AuthState()
    data class Success(val user: AuthUserResponse) : AuthState()
    data class Error(val error: Throwable) : AuthState()
}

class AuthScreenModel(private val navigator: Navigator, private val queryBus: QueryBus) : ScreenModel {
    private var _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> get() = _authState

    fun resetAuthForm() {
        _authState.value = AuthState.Idle
    }

    fun login(username: String, password: String) {
        _authState.value = AuthState.InProgress
        screenModelScope.launch(Dispatchers.IO) {
            val isUsernameInvalid = username.isEmpty()
            val isPasswordInvalid = password.isEmpty()

            if (isUsernameInvalid || isPasswordInvalid) {
                _authState.value = AuthState.Error(InvalidCredentialsException(isUsernameInvalid, isPasswordInvalid))
                return@launch
            }

            try {
                val authUserResponse: AuthUserResponse = queryBus.ask(AuthenticateUserQuery(username, password))
                _authState.value = AuthState.Success(authUserResponse)
            } catch (e: QueryHandlerExecutionError) {
                _authState.value = AuthState.Error(e.cause!!)
            }
        }
    }

    fun navigateToHome(user: AuthUserResponse) {
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
