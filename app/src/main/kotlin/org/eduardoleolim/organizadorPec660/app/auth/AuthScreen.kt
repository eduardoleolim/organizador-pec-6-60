package org.eduardoleolim.organizadorPec660.app.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.app.generated.resources.Res
import org.eduardoleolim.app.generated.resources.login_background
import org.eduardoleolim.organizadorPec660.core.auth.domain.InvalidAuthCredentialsError
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

class AuthScreen(private val window: ComposeWindow, private val queryBus: QueryBus) : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AuthScreenModel(navigator, queryBus) }

        LaunchedEffect(Unit) {
            window.apply {
                isResizable = false
                size = Dimension(800, 600)
                setLocationRelativeTo(null)
            }
        }

        Row {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(Res.drawable.login_background),
                    contentDescription = "Fuente de los 4 ríos",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AuthForm(screenModel)
            }
        }
    }

    @Composable
    private fun AuthForm(screenModel: AuthScreenModel) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        var isPasswordVisible by remember { mutableStateOf(false) }
        val visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        val trailingIcon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

        var enabled by remember { mutableStateOf(true) }
        var isUsernameError by remember { mutableStateOf(false) }
        var isPasswordError by remember { mutableStateOf(false) }
        var isCredentialsError by remember { mutableStateOf(false) }
        var usernameSupportingText by remember { mutableStateOf(null as String?) }
        var passwordSupportingText by remember { mutableStateOf(null as String?) }

        when (val authState = screenModel.authState.value) {
            AuthState.Idle -> {
                enabled = true
                isPasswordError = false
                isUsernameError = false
                isCredentialsError = false
                usernameSupportingText = null
                passwordSupportingText = null
            }

            AuthState.InProgress -> {
                enabled = false
                isPasswordError = false
                isUsernameError = false
                isCredentialsError = false
                usernameSupportingText = null
                passwordSupportingText = null
            }

            is AuthState.Success -> {
                screenModel.HomeView(authState.user)
                screenModel.resetAuthForm()
            }

            is AuthState.Error -> {
                enabled = true
                isPasswordError = false
                isUsernameError = false
                isCredentialsError = false
                usernameSupportingText = null
                passwordSupportingText = null

                when (val error = authState.error) {
                    is InvalidAuthCredentialsError -> {
                        isCredentialsError = true
                        isPasswordError = true
                        isUsernameError = true
                    }

                    is InvalidCredentialsException -> {
                        if (error.isUsernameInvalid) {
                            isUsernameError = true
                            usernameSupportingText = "El usuario es requerido"
                        }

                        if (error.isPasswordInvalid) {
                            isPasswordError = true
                            passwordSupportingText = "La contraseña es requerida"
                        }
                    }

                    else -> {
                        println("Error: ${error.message}")
                    }
                }
            }
        }

        Text(
            text = "Organizador PEC-6-60",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Text(
            text = "Ingresar",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (isCredentialsError) {
            Text(
                text = "Credenciales inválidas",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        OutlinedTextField(
            enabled = enabled,
            label = { Text("Usuario") },
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp),
            singleLine = true,
            isError = isUsernameError || isCredentialsError,
            supportingText = usernameSupportingText?.let { message ->
                { Text(text = message, color = MaterialTheme.colorScheme.error) }
            }
        )

        OutlinedTextField(
            enabled = enabled,
            label = { Text("Contraseña") },
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp),
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

        Button(
            enabled = enabled,
            onClick = { screenModel.login(username, password) },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Iniciar sesión")
        }
    }
}
