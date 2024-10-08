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

package org.eduardoleolim.organizadorpec660.auth.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import org.eduardoleolim.organizadorpec660.auth.domain.InvalidAuthCredentialsError
import org.eduardoleolim.organizadorpec660.auth.model.AuthScreenModel
import org.eduardoleolim.organizadorpec660.auth.model.AuthState
import org.eduardoleolim.organizadorpec660.auth.model.InvalidCredentialsException
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.shared.utils.generateErrorsLog
import org.eduardoleolim.window.LocalWindow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

class AuthScreen(private val queryBus: QueryBus) : Screen {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val window = LocalWindow.current
        val windowSize = calculateWindowSizeClass()
        val density = LocalDensity.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AuthScreenModel(navigator, queryBus, Dispatchers.IO) }

        LaunchedEffect(Unit) {
            val dimension = with(density) { Dimension(800.dp.roundToPx(), 600.dp.roundToPx()) }
            window.apply {
                minimumSize = dimension
                size = dimension
                isResizable = false
                setLocationRelativeTo(null)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            AnimatedVisibility(
                visible = windowSize.widthSizeClass != WindowWidthSizeClass.Compact,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(Res.drawable.login_background),
                    contentDescription = "Fountain of the Four Rivers",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            AuthForm(
                modifier = Modifier.weight(1f),
                screenModel = screenModel
            )
        }
    }

    @Composable
    private fun AuthForm(
        modifier: Modifier = Modifier,
        screenModel: AuthScreenModel
    ) {
        val credentials = screenModel.credentials
        val appVersion = screenModel.appVersion

        var isPasswordVisible by remember { mutableStateOf(false) }
        val visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        val trailingIcon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

        var enabled by remember { mutableStateOf(true) }
        var isUsernameError by remember { mutableStateOf(false) }
        var isPasswordError by remember { mutableStateOf(false) }
        var isCredentialsError by remember { mutableStateOf(false) }
        var usernameSupportingText by remember { mutableStateOf<String?>(null) }
        var passwordSupportingText by remember { mutableStateOf<String?>(null) }
        var loginErrorText by remember { mutableStateOf<String?>(null) }

        when (val authState = screenModel.authState) {
            AuthState.Idle -> {
                enabled = true
                isPasswordError = false
                isUsernameError = false
                isCredentialsError = false
                usernameSupportingText = null
                passwordSupportingText = null
                loginErrorText = null
            }

            AuthState.InProgress -> {
                enabled = false
                isPasswordError = false
                isUsernameError = false
                isCredentialsError = false
                usernameSupportingText = null
                passwordSupportingText = null
                loginErrorText = null
            }

            is AuthState.Success -> {
                screenModel.navigateToHomeScreen(authState.user)
            }

            is AuthState.Error -> {
                enabled = true

                when (val error = authState.error) {
                    is InvalidAuthCredentialsError -> {
                        isCredentialsError = true
                        isPasswordError = true
                        isUsernameError = true
                        loginErrorText = stringResource(Res.string.auth_invalid_credentials)
                    }

                    is InvalidCredentialsException -> {
                        if (error.isUsernameEmpty) {
                            isUsernameError = true
                            usernameSupportingText = stringResource(Res.string.auth_username_required)
                        }

                        if (error.isPasswordEmpty) {
                            isPasswordError = true
                            passwordSupportingText = stringResource(Res.string.auth_password_required)
                        }
                    }

                    else -> {
                        isCredentialsError = true
                        loginErrorText = stringResource(Res.string.default_error_message)
                        generateErrorsLog("auth", error)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .then(modifier),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "v$appVersion",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.auth_enter),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(isCredentialsError && loginErrorText != null) {
                Text(
                    text = loginErrorText ?: "",
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(24.dp))
            }

            OutlinedTextField(
                enabled = enabled,
                label = { Text(stringResource(Res.string.auth_username)) },
                value = credentials.username,
                onValueChange = { screenModel.updateUsername(it) },
                modifier = Modifier.width(320.dp),
                singleLine = true,
                isError = isUsernameError || isCredentialsError,
                supportingText = usernameSupportingText?.let { message ->
                    { Text(text = message, color = MaterialTheme.colorScheme.error) }
                }
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                enabled = enabled,
                label = { Text(stringResource(Res.string.auth_password)) },
                value = credentials.password,
                onValueChange = { screenModel.updatePassword(it) },
                modifier = Modifier.width(320.dp),
                singleLine = true,
                isError = isPasswordError || isCredentialsError,
                supportingText = passwordSupportingText?.let { message ->
                    { Text(text = message, color = MaterialTheme.colorScheme.error) }
                },
                visualTransformation = visualTransformation,
                trailingIcon = {
                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible },
                        modifier = Modifier.pointerHoverIcon(PointerIcon.Default)
                    ) {
                        Icon(imageVector = trailingIcon, contentDescription = "Password visibility")
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                enabled = enabled,
                onClick = { screenModel.login() },
                modifier = Modifier.width(320.dp),
            ) {
                if (screenModel.authState != AuthState.InProgress && enabled) {
                    Text(stringResource(Res.string.auth_login))
                } else {
                    CircularProgressIndicator(Modifier.size(16.dp))
                }
            }
        }
    }
}
