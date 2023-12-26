package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.app.shared.utils.WindowSize
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

enum class HomeScreenTab {
    ENTIDADES_FEDERATIVAS,
    MUNICIPIOS,
    TIPOS_DE_ESTADISTICA,
    TIPOS_DE_INSTRUMENTO
}

class HomeScreen(
    private val window: ComposeWindow,
    private val user: AuthUserResponse
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HomeScreenModel(navigator) }
        var selectedTab by remember { mutableStateOf(HomeScreenTab.ENTIDADES_FEDERATIVAS) }
        var windowSize by remember(window) { mutableStateOf(WindowSize.fromWidth(window.size.width.dp)) }

        LaunchedEffect(Unit) {
            window.apply {
                isResizable = true
                size = Dimension(800, 600)
                minimumSize = Dimension(800, 600)
                setLocationRelativeTo(null)
            }
        }

        DisposableEffect(Unit) {
            val listener = object : ComponentAdapter() {
                override fun componentResized(event: ComponentEvent) {
                    windowSize = WindowSize.fromWidth(event.component.size.width.dp)
                }
            }

            window.addComponentListener(listener)
            onDispose {
                window.removeComponentListener(listener)
            }
        }

        when (windowSize) {
            WindowSize.COMPACT, WindowSize.MEDIUM -> MediumMenu(
                screenModel = screenModel,
                selectedTab = selectedTab,
                onSelectedTabChange = { selectedTab = it }
            ) {
                WorkArea(screenModel = screenModel, selectedTab = selectedTab)
            }

            WindowSize.EXPANDED -> ExpandedMenu(
                screenModel = screenModel,
                selectedTab = selectedTab,
                onSelectedTabChange = { selectedTab = it }
            ) {
                WorkArea(screenModel = screenModel, selectedTab = selectedTab)
            }
        }
    }

    @Composable
    private fun MediumMenu(
        screenModel: HomeScreenModel,
        selectedTab: HomeScreenTab,
        onSelectedTabChange: (HomeScreenTab) -> Unit,
        content: @Composable () -> Unit
    ) {
        Row {
            NavigationRail(
                modifier = Modifier.padding(8.dp),
                header = {
                    IconButton(
                        onClick = { },
                        content = { Icon(Icons.Default.Menu, contentDescription = null) }
                    )
                }
            ) {
                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.ENTIDADES_FEDERATIVAS,
                    onClick = { onSelectedTabChange(HomeScreenTab.ENTIDADES_FEDERATIVAS) },
                    modifier = Modifier.height(72.dp),
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text(text = "Entidades\nfederativas", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.MUNICIPIOS,
                    onClick = { onSelectedTabChange(HomeScreenTab.MUNICIPIOS) },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == HomeScreenTab.MUNICIPIOS) Icons.Default.ListAlt else Icons.Outlined.ListAlt,
                            contentDescription = null
                        )
                    },
                    label = { Text(text = "Municípios", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.TIPOS_DE_ESTADISTICA,
                    onClick = { onSelectedTabChange(HomeScreenTab.TIPOS_DE_ESTADISTICA) },
                    modifier = Modifier.height(72.dp),
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text(text = "Tipos de\nestadísticas", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = selectedTab == HomeScreenTab.TIPOS_DE_INSTRUMENTO,
                    onClick = { onSelectedTabChange(HomeScreenTab.TIPOS_DE_INSTRUMENTO) },
                    modifier = Modifier.height(72.dp),
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                    label = { Text(text = "Tipos de\ninstrumentos", textAlign = TextAlign.Center) }
                )

                NavigationRailItem(
                    selected = false,
                    onClick = { screenModel.logout() },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                    label = { Text("Cerrar sesión") }
                )
            }

            content()
        }
    }

    @Composable
    private fun ExpandedMenu(
        screenModel: HomeScreenModel,
        selectedTab: HomeScreenTab,
        onSelectedTabChange: (HomeScreenTab) -> Unit,
        content: @Composable () -> Unit
    ) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(Modifier.width(240.dp)) {
                    Spacer(Modifier.height(12.dp))

                    NavigationDrawerItem(
                        selected = selectedTab == HomeScreenTab.ENTIDADES_FEDERATIVAS,
                        onClick = { onSelectedTabChange(HomeScreenTab.ENTIDADES_FEDERATIVAS) },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                        label = { Text(text = "Entidades federativas") }
                    )

                    NavigationDrawerItem(
                        selected = selectedTab == HomeScreenTab.MUNICIPIOS,
                        onClick = { onSelectedTabChange(HomeScreenTab.MUNICIPIOS) },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                        label = { Text(text = "Municípios") }
                    )

                    NavigationDrawerItem(
                        selected = selectedTab == HomeScreenTab.TIPOS_DE_ESTADISTICA,
                        onClick = { onSelectedTabChange(HomeScreenTab.TIPOS_DE_ESTADISTICA) },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                        label = { Text(text = "Tipos de estadísticas") }
                    )

                    NavigationDrawerItem(
                        selected = selectedTab == HomeScreenTab.TIPOS_DE_INSTRUMENTO,
                        onClick = { onSelectedTabChange(HomeScreenTab.TIPOS_DE_INSTRUMENTO) },
                        icon = { Icon(Icons.Default.ListAlt, contentDescription = null) },
                        label = { Text(text = "Tipos de instrumentos") }
                    )

                    Spacer(Modifier.height(12.dp))

                    Divider()

                    Spacer(Modifier.height(12.dp))

                    NavigationDrawerItem(
                        selected = false,
                        onClick = { screenModel.logout() },
                        icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        label = { Text("Cerrar sesión") }
                    )
                }
            },
            content = content
        )
    }

    @Composable
    private fun WorkArea(
        screenModel: HomeScreenModel,
        selectedTab: HomeScreenTab,
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            when (selectedTab) {
                HomeScreenTab.ENTIDADES_FEDERATIVAS -> screenModel.navigateToFederalEntity()

                HomeScreenTab.MUNICIPIOS -> screenModel.navigateToMunicipality()

                HomeScreenTab.TIPOS_DE_ESTADISTICA -> screenModel.navigateToStatisticType()

                HomeScreenTab.TIPOS_DE_INSTRUMENTO -> screenModel.navigateToInstrumentType()
            }
        }
    }
}
