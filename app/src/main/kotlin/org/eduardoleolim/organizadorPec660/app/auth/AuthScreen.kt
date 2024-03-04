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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorPec660.core.auth.domain.InvalidAuthCredentialsError
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus
import java.awt.Dimension

class AuthScreen(private val window: ComposeWindow, private val queryBus: QueryBus) : Screen {
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
                    painter = painterResource("drawable/login_background.png"),
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
                screenModel.resetAuthForm()
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
        var areInvalidCredentials by remember { mutableStateOf(false) }
        var usernameSupportingText: (@Composable () -> Unit)? by remember { mutableStateOf(null) }
        var passwordSupportingText: (@Composable () -> Unit)? by remember { mutableStateOf(null) }

        when (val authState = screenModel.authState.value) {
            AuthState.Idle -> {
                enabled = true
                isPasswordError = false
                isUsernameError = false
                areInvalidCredentials = false
                usernameSupportingText = null
                passwordSupportingText = null
            }

            AuthState.InProgress -> {
                enabled = false
                isPasswordError = false
                isUsernameError = false
                areInvalidCredentials = false
                usernameSupportingText = null
                passwordSupportingText = null
            }

            is AuthState.Success -> {
                screenModel.navigateToHome(authState.user)
            }

            is AuthState.Error -> {
                enabled = true
                var usernameMessage: String? = null
                var passwordMessage: String? = null

                when (val error = authState.error) {
                    is InvalidAuthCredentialsError -> {
                        areInvalidCredentials = true
                    }

                    is InvalidCredentialsException -> {
                        if (error.isUsernameInvalid) {
                            isUsernameError = true
                            usernameMessage = "El usuario es requerido"
                        }

                        if (error.isPasswordInvalid) {
                            isPasswordError = true
                            passwordMessage = "La contraseña es requerida"
                        }
                    }

                    else -> {
                        println("Error: ${error.message}")
                    }
                }

                passwordSupportingText = passwordMessage?.let { message ->
                    {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                usernameSupportingText = usernameMessage?.let { message ->
                    {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Text(
            text = "Organizador\nPEC-6-60",
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Text(
            text = "Ingresar",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (areInvalidCredentials) {
            Text(
                text = "Credenciales inválidas",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            enabled = enabled,
            isError = isUsernameError || areInvalidCredentials,
            supportingText = usernameSupportingText,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            enabled = enabled,
            isError = isPasswordError || areInvalidCredentials,
            supportingText = passwordSupportingText,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp),
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
            onClick = {
                screenModel.login(username, password)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Iniciar sesión", fontSize = 16.sp)
        }
    }
}
