package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.vector.ImageVector
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
    INSTRUMENTOS,
    ENTIDADES_FEDERATIVAS,
    MUNICIPIOS,
    TIPOS_DE_ESTADISTICA,
    TIPOS_DE_INSTRUMENTO
}

private class MenuItem(
    val tab: HomeScreenTab,
    val icon: @Composable () -> Unit,
    val label: @Composable () -> Unit,
    val modifier: Modifier = Modifier
)

class HomeScreen(
    private val window: ComposeWindow,
    private val user: AuthUserResponse
) : Screen {
    private val items = listOf(
        Triple("Instrumentos", Icons.Default.ListAlt, HomeScreenTab.INSTRUMENTOS),
        Triple("Entidades Federativas", Icons.Default.ListAlt, HomeScreenTab.ENTIDADES_FEDERATIVAS),
        Triple("Municipios", Icons.Default.ListAlt, HomeScreenTab.MUNICIPIOS),
        Triple("Tipos de Estadística", Icons.Default.ListAlt, HomeScreenTab.TIPOS_DE_ESTADISTICA),
        Triple("Tipos de Instrumento", Icons.Default.ListAlt, HomeScreenTab.TIPOS_DE_INSTRUMENTO)
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val compositionContext = rememberCompositionContext()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val screenModel = rememberScreenModel { HomeScreenModel(navigator, drawerState, compositionContext) }
        var selectedTab by remember { mutableStateOf(HomeScreenTab.INSTRUMENTOS) }
        var windowSize by remember { mutableStateOf(WindowSize.fromWidth(window.size.width.dp)) }
        val homeConfig = remember { HomeConfig() }

        DisposableEffect(Unit) {
            window.apply {
                isResizable = true
                size = Dimension(800, 600)
                minimumSize = Dimension(800, 600)
                setLocationRelativeTo(null)
            }

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

        CompositionLocalProvider(
            LocalHomeConfig provides homeConfig
        ) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalNavigationDrawerContent(
                        items = items,
                        screenModel = screenModel,
                        selectedTab = selectedTab,
                        onChangeSelectedTab = {
                            screenModel.closeNavigationDrawer(onOpen = { selectedTab = it })
                        }
                    )
                },
            ) {
                Scaffold(
                    topBar = {
                        val actions = homeConfig.actions
                        val title = homeConfig.title

                        TopAppBar(
                            modifier = Modifier.padding(16.dp),
                            title = { Text(title) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { screenModel.openNavigationDrawer() },
                                ) {
                                    Icon(Icons.Filled.Menu, contentDescription = null)
                                }
                            },
                            actions = {
                                actions()
                            }
                        )
                    }
                ) { paddingValues ->
                    WorkArea(
                        screenModel = screenModel,
                        selectedTab = selectedTab,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }

    @Composable
    private fun ModalNavigationDrawerContent(
        items: List<Triple<String, ImageVector, HomeScreenTab>>,
        screenModel: HomeScreenModel,
        selectedTab: HomeScreenTab,
        onChangeSelectedTab: (HomeScreenTab) -> Unit
    ) {
        ModalDrawerSheet(
            modifier = Modifier.width(300.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Organizador PEC-6-60")
                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(
                    onClick = { screenModel.closeNavigationDrawer() }
                ) {
                    Icon(Icons.Filled.MenuOpen, contentDescription = null)
                }
            }

            Divider()
            items.forEach {
                val (label, icon, tab) = it
                NavigationDrawerItem(
                    icon = { Icon(icon, contentDescription = null) },
                    label = { Text(text = label) },
                    selected = selectedTab == tab,
                    onClick = { onChangeSelectedTab(tab) }
                )
            }

            Spacer(modifier = Modifier.weight(1.0f))
            Divider()
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                label = { Text(text = "Cerrar Sesión") },
                selected = false,
                onClick = { screenModel.logout() }
            )
        }
    }

    @Composable
    private fun WorkArea(screenModel: HomeScreenModel, selectedTab: HomeScreenTab, paddingValues: PaddingValues) {
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            screenModel.apply {
                when (selectedTab) {
                    HomeScreenTab.INSTRUMENTOS -> NavigateToInstrument()
                    HomeScreenTab.ENTIDADES_FEDERATIVAS -> NavigateToFederalEntity()
                    HomeScreenTab.MUNICIPIOS -> NavigateToMunicipality()
                    HomeScreenTab.TIPOS_DE_ESTADISTICA -> NavigateToStatisticType()
                    HomeScreenTab.TIPOS_DE_INSTRUMENTO -> NavigateToInstrumentType()
                }
            }
        }
    }
}

class HomeConfig {
    var title by mutableStateOf("")
    var actions by mutableStateOf<@Composable RowScope.() -> Unit>(@Composable {})
}

private val LocalHomeConfig = compositionLocalOf<HomeConfig> { error("home actions not provided") }

@Composable
fun HomeTitle(title: String) {
    val current = LocalHomeConfig.current
    current.title = title

    DisposableEffect(Unit) {
        onDispose {
            current.title = ""
        }
    }
}

@Composable
fun HomeActions(actions: @Composable RowScope.() -> Unit) {
    val current = LocalHomeConfig.current
    current.actions = actions

    DisposableEffect(Unit) {
        onDispose {
            current.actions = @Composable {}
        }
    }
}
