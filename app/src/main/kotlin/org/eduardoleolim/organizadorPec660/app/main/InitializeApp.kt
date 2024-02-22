package org.eduardoleolim.organizadorPec660.app.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.jthemedetecor.OsThemeDetector
import org.eduardoleolim.organizadorPec660.app.main.customWindow.CustomWindow
import org.eduardoleolim.organizadorPec660.app.main.customWindow.WindowIcon
import org.eduardoleolim.organizadorPec660.app.shared.theme.DarkColors
import org.eduardoleolim.organizadorPec660.app.shared.theme.LightColors
import java.awt.Dimension
import java.util.function.Consumer
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

class InitializeApp {
    fun start(
        onPasswordSet: (String) -> Unit
    ) = application(exitProcessOnExit = false) {

        val state = rememberWindowState()
        var isDarkTheme by remember { mutableStateOf(true) }

        DisposableEffect(Unit) {
            val osThemeDetector = OsThemeDetector.getDetector()
            isDarkTheme = osThemeDetector.isDark

            val consumer = Consumer<Boolean> {
                SwingUtilities.invokeLater {
                    isDarkTheme = it
                }
            }

            osThemeDetector.registerListener(consumer)
            onDispose {
                osThemeDetector.removeListener(consumer)
            }
        }

        MaterialTheme(
            colorScheme = if (isDarkTheme) DarkColors else LightColors
        ) {
            val icon = painterResource("assets/icon.png")
            CustomWindow(
                state = state,
                onCloseRequest = {
                    exitApplication()
                    exitProcess(0)
                },
                onRequestToggleMaximize = null,
                defaultTitle = "Organizador PEC-6-60",
                defaultIcon = icon
            ) {
                LaunchedEffect(Unit) {
                    window.apply {
                        isResizable = false
                        size = Dimension(400, 400)
                        setLocationRelativeTo(null)
                        toFront()
                    }
                }

                WindowIcon(icon) {}

                Content(
                    onPasswordSet = {
                        onPasswordSet(it)
                        exitApplication()
                    }
                )
            }
        }
    }

    @Composable
    private fun Content(
        onPasswordSet: (String) -> Unit
    ) {
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido a Organizador PEC-6-60!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Para comenzar, establezca una contraseña para la base de datos",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onPasswordSet(password) },
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    text = "Guardar",
                    fontSize = 16.sp
                )
            }
        }
    }
}
