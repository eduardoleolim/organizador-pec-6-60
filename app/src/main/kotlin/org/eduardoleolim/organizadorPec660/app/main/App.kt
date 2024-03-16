package org.eduardoleolim.organizadorPec660.app.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch
import org.eduardoleolim.app.generated.resources.Res
import org.eduardoleolim.app.generated.resources.logo
import org.eduardoleolim.organizadorPec660.app.main.customWindow.*
import org.eduardoleolim.organizadorPec660.app.main.router.Router
import org.eduardoleolim.organizadorPec660.app.shared.theme.DarkColors
import org.eduardoleolim.organizadorPec660.app.shared.theme.LightColors
import org.eduardoleolim.organizadorPec660.app.shared.theme.RobotoTypography
import org.eduardoleolim.organizadorPec660.app.shared.utils.AppUtils
import org.eduardoleolim.organizadorPec660.app.shared.utils.isSystemInDarkTheme
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.SqliteKtormDatabase
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.awt.Dimension

enum class SystemTheme {
    DARK, LIGHT, DEFAULT
}

class App(
    private val databasePath: String,
    private var sqlitePassword: String,
    private var sqliteExtensions: List<String>
) {
    fun start() = application {
        var initializeApp by remember { mutableStateOf(SqliteKtormDatabase.exists(databasePath)) }

        if (initializeApp) {
            MainWindow(
                onCloseRequest = { exitApplication() }
            )
        } else {
            ConfigWindow(
                onCloseRequest = { exitApplication() },
                onPasswordSet = { password ->
                    AppUtils.sqlitePassword(password)
                    sqlitePassword = password
                    initializeApp = true
                }
            )
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun MainWindow(onCloseRequest: () -> Unit) {
        val state = rememberWindowState()
        var selectedTheme by remember { mutableStateOf(SystemTheme.DEFAULT) }
        val isSystemInDarkTheme = isSystemInDarkTheme()
        val commandBus: CommandBus = remember {
            KtormCommandBus(SqliteKtormDatabase.connect(databasePath, sqlitePassword, sqliteExtensions))
        }
        val queryBus = remember {
            KtormQueryBus(SqliteKtormDatabase.connectReadOnly(databasePath, sqlitePassword, sqliteExtensions))
        }

        MaterialTheme(
            typography = RobotoTypography,
            colorScheme = when (selectedTheme) {
                SystemTheme.DARK -> DarkColors
                SystemTheme.LIGHT -> LightColors
                SystemTheme.DEFAULT -> if (isSystemInDarkTheme) DarkColors else LightColors
            }
        ) {
            val icon = painterResource(Res.drawable.logo)
            CustomWindow(
                state = state,
                onCloseRequest = { onCloseRequest() },
                defaultTitle = "Organizador PEC-6-60",
                defaultIcon = icon
            ) {
                WindowIcon(icon) {}
                WindowCenter {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 16.dp),
                    ) {
                        Icon(
                            imageVector = when (selectedTheme) {
                                SystemTheme.DARK -> Icons.Filled.DarkMode
                                SystemTheme.LIGHT -> Icons.Filled.LightMode
                                SystemTheme.DEFAULT -> Icons.Filled.Contrast
                            },
                            contentDescription = "Toggle theme",
                            modifier = Modifier
                                .windowFrameItem("theme", HitSpots.OTHER_HIT_SPOT)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedTheme = when (selectedTheme) {
                                        SystemTheme.DARK -> SystemTheme.LIGHT
                                        SystemTheme.LIGHT -> SystemTheme.DEFAULT
                                        SystemTheme.DEFAULT -> SystemTheme.DARK
                                    }
                                }
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Router(commandBus, queryBus)
                }
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun ConfigWindow(onCloseRequest: () -> Unit, onPasswordSet: (String) -> Unit) {
        val state = rememberWindowState()
        val isSystemInDarkTheme = isSystemInDarkTheme()

        MaterialTheme(
            colorScheme = if (isSystemInDarkTheme) DarkColors else LightColors
        ) {
            val icon = painterResource(Res.drawable.logo)
            CustomWindow(
                state = state,
                onCloseRequest = onCloseRequest,
                onRequestToggleMaximize = null,
                defaultTitle = "Organizador PEC-6-60",
                defaultIcon = icon
            ) {
                LaunchedEffect(Unit) {
                    window.apply {
                        isResizable = false
                        size = Dimension(400, 400)
                        setLocationRelativeTo(null)
                    }
                }

                WindowIcon(icon) {}

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    var password by remember { mutableStateOf("") }

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
                        onClick = {
                            coroutineScope.launch {

                            }
                            onPasswordSet(password)
                        },
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
    }
}
