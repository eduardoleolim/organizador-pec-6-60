/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormCommandBus
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormQueryBus
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.SqliteKtormDatabase
import org.eduardoleolim.organizadorpec660.shared.notification.LocalTrayState
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.shared.router.Router
import org.eduardoleolim.organizadorpec660.shared.theme.AppTheme
import org.eduardoleolim.organizadorpec660.shared.theme.Contrast
import org.eduardoleolim.organizadorpec660.shared.utils.AppConfig
import org.eduardoleolim.window.DecoratedWindow
import org.eduardoleolim.window.DesktopPlatform
import org.eduardoleolim.window.TitleBar
import org.eduardoleolim.window.isSystemInDarkTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import java.io.File

enum class SystemTheme {
    DARK, LIGHT, DARK_MEDIUM, LIGHT_MEDIUM, DARK_HIGH, LIGHT_HIGH, DEFAULT
}

class App(
    private val databaseDirectory: String,
    private var databasePassword: String,
    private val databaseExtensionsDirectory: String,
    private val instrumentsDirectory: String,
    private val tempDirectory: String
) {
    private val databaseExtensions by lazy {
        File(databaseExtensionsDirectory).listFiles()?.map { it.absolutePath } ?: emptyList()
    }

    private val commandBus: CommandBus by lazy {
        val database = SqliteKtormDatabase.connect(databaseDirectory, databasePassword, databaseExtensions)
        KtormCommandBus(database, instrumentsDirectory)
    }

    private val queryBus: QueryBus by lazy {
        val database = SqliteKtormDatabase.connectReadOnly(databaseDirectory, databasePassword, databaseExtensions)
        KtormQueryBus(database, instrumentsDirectory)
    }

    private fun createTempDirectory() {
        File(tempDirectory).mkdirs()
    }

    private fun deleteTempDirectory() {
        File(tempDirectory).deleteRecursively()
    }

    fun start() = application {
        var existsDatabase by remember { mutableStateOf(SqliteKtormDatabase.exists(databaseDirectory)) }

        LaunchedEffect(Unit) {
            createTempDirectory()
        }

        if (existsDatabase) {
            MainWindow(
                onCloseRequest = {
                    deleteTempDirectory()
                    exitApplication()
                }
            )
        } else {
            ConfigWindow(
                onCloseRequest = {
                    deleteTempDirectory()
                    exitApplication()
                },
                onPasswordSet = { password ->
                    AppConfig["app.database.password"] = password
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

        AppTheme(isDarkMode = isSystemInDarkTheme) {
            val icon = painterResource(Res.drawable.logo)
            DecoratedWindow(
                state = state,
                onCloseRequest = onCloseRequest,
                title = stringResource(Res.string.app_name),
                icon = icon
            ) {
                val density = LocalDensity.current
                var isPasswordVisible by remember { mutableStateOf(false) }
                val visualTransformation =
                    if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                val trailingIcon = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                LaunchedEffect(Unit) {
                    window.apply {
                        isResizable = false
                        size = with(density) { Dimension(400.dp.roundToPx(), 400.dp.roundToPx()) }
                        setLocationRelativeTo(null)
                    }
                }

                TitleBar(
                    onCloseRequest = onCloseRequest
                ) {
                    if (DesktopPlatform.Current != DesktopPlatform.MacOS) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 10.dp)
                        ) {
                            Image(
                                painter = icon,
                                contentDescription = "icon",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(title)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
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
                            text = stringResource(Res.string.init_screen_title),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = stringResource(Res.string.init_request_password),
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
                            singleLine = true,
                            label = {
                                Text(stringResource(Res.string.password))
                            },
                            visualTransformation = visualTransformation,
                            trailingIcon = {
                                IconButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible },
                                    modifier = Modifier
                                        .focusProperties { canFocus = false }
                                        .pointerHoverIcon(PointerIcon.Default)
                                ) {
                                    Icon(imageVector = trailingIcon, contentDescription = "Password visibility")
                                }
                            }
                        )

                        Spacer(Modifier.height(32.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    onPasswordSet(password)
                                }
                            },
                            modifier = Modifier.width(280.dp)
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
    }

    @Composable
    private fun ApplicationScope.MainWindow(onCloseRequest: () -> Unit) {
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
            val trayState = rememberTrayState()

            Tray(
                icon = icon,
                state = trayState
            )

            CompositionLocalProvider(
                LocalTrayState provides trayState
            ) {
                DecoratedWindow(
                    onCloseRequest = onCloseRequest,
                    state = state,
                    icon = icon,
                    title = stringResource(Res.string.app_name)
                ) {
                    TitleBar(
                        onCloseRequest = onCloseRequest
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(start = 10.dp)
                        ) {
                            if (DesktopPlatform.Current != DesktopPlatform.MacOS) {
                                Image(
                                    painter = icon,
                                    contentDescription = "icon",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

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
                        Router(
                            commandBus = commandBus,
                            queryBus = queryBus,
                            tempDirectory = tempDirectory
                        )
                    }
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
