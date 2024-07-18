package org.eduardoleolim.organizadorpec660.app

import androidx.compose.foundation.Image
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
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.router.Router
import org.eduardoleolim.organizadorpec660.app.shared.theme.AppTheme
import org.eduardoleolim.organizadorpec660.app.shared.theme.Contrast
import org.eduardoleolim.organizadorpec660.app.shared.utils.AppConfig
import org.eduardoleolim.organizadorpec660.app.shared.utils.isSystemInDarkTheme
import org.eduardoleolim.organizadorpec660.app.window.CustomWindow
import org.eduardoleolim.organizadorpec660.app.window.DecoratedWindow
import org.eduardoleolim.organizadorpec660.app.window.TitleBar
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.SqliteKtormDatabase
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import java.io.File

enum class SystemTheme {
    DARK, LIGHT, DARK_MEDIUM, LIGHT_MEDIUM, DARK_HIGH, LIGHT_HIGH, DEFAULT
}

class App(
    private val databasePath: String,
    private var databasePassword: String,
    private val databaseExtensionPath: String,
    private val instrumentsPath: String
) {
    private val databaseExtensions by lazy {
        File(databaseExtensionPath).listFiles()?.map { it.absolutePath } ?: emptyList()
    }

    private val commandBus: CommandBus by lazy {
        val database = SqliteKtormDatabase.connect(databasePath, databasePassword, databaseExtensions)
        KtormCommandBus(database, instrumentsPath)
    }

    private val queryBus: QueryBus by lazy {
        val database = SqliteKtormDatabase.connectReadOnly(databasePath, databasePassword, databaseExtensions)
        KtormQueryBus(database, instrumentsPath)
    }

    fun start() = application {
        var existsDatabase by remember { mutableStateOf(SqliteKtormDatabase.exists(databasePath)) }

        if (existsDatabase) {
            MainWindow(onCloseRequest = { exitApplication() })
        } else {
            ConfigWindow(
                onCloseRequest = { exitApplication() },
                onPasswordSet = { password ->
                    AppConfig["database.password"] = password
                    databasePassword = password
                    existsDatabase = true
                }
            )
        }
    }

    @Composable
    private fun ConfigWindow(onCloseRequest: () -> Unit, onPasswordSet: (String) -> Unit) {
        val state = rememberWindowState()
        val isSystemInDarkTheme = isSystemInDarkTheme()

        AppTheme(
            isDarkMode = isSystemInDarkTheme
        ) {
            val icon = painterResource(Res.drawable.logo)
            CustomWindow(
                state = state,
                onCloseRequest = onCloseRequest,
                onRequestToggleMaximize = null,
                defaultTitle = stringResource(Res.string.app_name),
                defaultIcon = icon
            ) {
                LaunchedEffect(Unit) {
                    window.apply {
                        isResizable = false
                        size = Dimension(400, 400)
                        setLocationRelativeTo(null)
                    }
                }

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

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Para comenzar, establezca una contraseña para la base de datos",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )

                    Spacer(Modifier.height(32.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(Res.string.password)) }
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {

                            }
                            onPasswordSet(password)
                        },
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.save),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun MainWindow(onCloseRequest: () -> Unit) {
        val state = rememberWindowState()
        var theme by remember { mutableStateOf(SystemTheme.DEFAULT) }
        val isSystemInDarkTheme = isSystemInDarkTheme()

        AppTheme(
            isDarkMode = when (theme) {
                SystemTheme.DARK, SystemTheme.DARK_MEDIUM, SystemTheme.DARK_HIGH -> true
                SystemTheme.LIGHT, SystemTheme.LIGHT_MEDIUM, SystemTheme.LIGHT_HIGH -> false
                SystemTheme.DEFAULT -> isSystemInDarkTheme
            },
            contrast = when (theme) {
                SystemTheme.DARK, SystemTheme.LIGHT, SystemTheme.DEFAULT -> Contrast.NORMAL
                SystemTheme.DARK_MEDIUM, SystemTheme.LIGHT_MEDIUM -> Contrast.MEDIUM
                SystemTheme.DARK_HIGH, SystemTheme.LIGHT_HIGH -> Contrast.HIGH
            }
        ) {
            val icon = painterResource(Res.drawable.logo)
            DecoratedWindow(
                onCloseRequest = onCloseRequest,
                state = state,
                icon = icon,
                title = stringResource(Res.string.app_name)
            ) {
                TitleBar {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.Start).padding(start = 10.dp)
                    ) {
                        Image(
                            painter = icon,
                            contentDescription = "icon",
                            modifier = Modifier.size(16.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ThemeSelector(onThemeSelected = { theme = it })
                        }
                    }

                    Text(title)
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

    @Composable
    private fun ThemeSelector(
        initialValue: SystemTheme = SystemTheme.DEFAULT,
        onThemeSelected: (SystemTheme) -> Unit
    ) {
        val themes = remember {
            listOf(
                Triple(Icons.Filled.Contrast, SystemTheme.DEFAULT, "System theme"),
                Triple(Icons.Filled.DarkMode, SystemTheme.DARK, "Dark theme"),
                Triple(Icons.Filled.DarkMode, SystemTheme.DARK_MEDIUM, "Medium Dark theme"),
                Triple(Icons.Filled.DarkMode, SystemTheme.DARK_HIGH, "High Dark theme"),
                Triple(Icons.Filled.LightMode, SystemTheme.LIGHT, "Light theme"),
                Triple(Icons.Filled.LightMode, SystemTheme.LIGHT_MEDIUM, "Medium Light theme"),
                Triple(Icons.Filled.LightMode, SystemTheme.LIGHT_HIGH, "High Light  theme"),
            )
        }
        var theme by remember {
            val value = themes.find { it.second == initialValue } ?: themes.first()
            mutableStateOf(value)
        }
        var expanded by remember { mutableStateOf(false) }

        val background = MaterialTheme.colorScheme.primaryContainer
        val foreground = LocalContentColor.current
        val fontStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Light)

        LaunchedEffect(theme) {
            onThemeSelected(theme.second)
        }

        Box {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { expanded = true }
                    .padding(4.dp)
                    .height(24.dp)
            ) {
                Icon(
                    imageVector = theme.first,
                    contentDescription = "Toggle theme",
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    color = foreground,
                    text = theme.third,
                    style = fontStyle
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = background
            ) {
                themes.forEach { item ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                tint = foreground,
                                imageVector = item.first,
                                contentDescription = item.third,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        text = {
                            Text(
                                color = foreground,
                                text = item.third,
                                style = fontStyle
                            )
                        },
                        onClick = {
                            expanded = false
                            theme = item
                        }
                    )
                }
            }
        }
    }
}
