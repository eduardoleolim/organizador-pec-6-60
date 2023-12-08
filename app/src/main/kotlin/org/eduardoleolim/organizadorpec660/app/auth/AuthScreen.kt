package org.eduardoleolim.organizadorpec660.app.auth

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
import androidx.compose.ui.window.WindowPlacement
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.app.main.router.MainProvider
import org.eduardoleolim.organizadorpec660.core.auth.domain.InvalidAuthCredentialsError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import java.awt.Dimension

class AuthScreen(private val window: ComposeWindow, private val queryBus: QueryBus) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { AuthScreenModel(queryBus) }
        window.apply {
            placement = WindowPlacement.Floating
            isResizable = false
            size = Dimension(800, 600)
        }

        Row {
            Column(modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight()) {
                Image(
                    painter = painterResource("assets/img/login_background.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginForm(screenModel)
            }
        }
    }

    @Composable
    private fun LoginForm(screenModel: AuthScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isUsernameError by remember { mutableStateOf(false) }
        var isPasswordError by remember { mutableStateOf(false) }
        var areInvalidCredentials by remember { mutableStateOf(false) }
        var passwordVisibility by remember { mutableStateOf(false) }
        val visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
        val trailingIcon = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

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
            isError = isUsernameError || areInvalidCredentials,
            supportingText = {
                if (username.isEmpty() && isUsernameError) {
                    Text(
                        text = "Debes proporcionar el usuario",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            isError = isPasswordError || areInvalidCredentials,
            supportingText = {
                if (password.isEmpty() && isPasswordError) {
                    Text(
                        text = "Debes proporcionar la contraseña",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp),
            visualTransformation = visualTransformation,
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility },
                    modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                ) {
                    Icon(imageVector = trailingIcon, contentDescription = null)
                }
            }
        )

        Button(
            onClick = {
                screenModel.login(username, password).fold(
                    onSuccess = {
                        navigator.push(ScreenRegistry.get(MainProvider.HomeScreen(it)))
                    },
                    onFailure = { error ->
                        when (error) {
                            is InvalidAuthCredentialsError -> {
                                areInvalidCredentials = true
                            }

                            is InvalidCredentialsException -> {
                                isUsernameError = error.isUsernameInvalid
                                isPasswordError = error.isPasswordInvalid
                            }
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Iniciar sesión")
        }
    }
}
