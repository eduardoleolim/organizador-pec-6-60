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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import java.awt.Dimension

class AuthScreen(
    private val window: ComposeWindow,
    private val commandBus: CommandBus,
    private val queryBus: QueryBus
) : Screen {
    @Composable
    override fun Content() {
        window.placement = WindowPlacement.Floating
        window.isResizable = false
        window.size = Dimension(800, 600)

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
                LoginForm()
            }
        }
    }

    @Composable
    private fun LoginForm() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { AuthScreenModel(navigator, commandBus, queryBus) }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
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

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            maxLines = 1,
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
            onClick = { screenModel.login(username, password) },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Iniciar sesión")
        }
    }
}
