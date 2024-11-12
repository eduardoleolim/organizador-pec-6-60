/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryNotRegisteredError
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

            authState = try {
                queryBus.ask(AuthenticateUserQuery(username, password)).fold(
                    ifRight = { user ->
                        AuthState.Success(user)
                    },
                    ifLeft = { error ->
                        AuthState.Error(error)
                    }
                )
            } catch (e: QueryNotRegisteredError) {
                AuthState.Error(e)
            }
        }
    }

    fun navigateToHomeScreen(user: AuthUserResponse) {
        authState = AuthState.Idle
        credentials = Credentials()
        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(user)))
    }
}
