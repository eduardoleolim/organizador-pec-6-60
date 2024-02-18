package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material.icons.outlined.ListAlt
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
import org.eduardoleolim.organizadorPec660.core.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.app.shared.utils.WindowSize
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

enum class MenuTab {
    INSTRUMENTOS,
    ENTIDADES_FEDERATIVAS,
    MUNICIPIOS,
    TIPOS_DE_ESTADISTICA,
    TIPOS_DE_INSTRUMENTO
}

class HomeScreen(
    private val window: ComposeWindow,
    private val user: AuthUserResponse
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val compositionContext = rememberCompositionContext()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val screenModel = rememberScreenModel { HomeScreenModel(navigator, drawerState, compositionContext) }
        var selectedTab by remember { mutableStateOf(MenuTab.INSTRUMENTOS) }
        var windowSize by remember { mutableStateOf(WindowSize.fromWidth(window.size.width.dp)) }
        val homeConfig = remember { HomeConfig() }
        val items = remember {
            listOf(
                Triple(
                    "Instrumentos",
                    Pair(Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
                    MenuTab.INSTRUMENTOS
                ),
                Triple(
                    "Entidades Federativas",
                    Pair(Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
                    MenuTab.ENTIDADES_FEDERATIVAS
                ),
                Triple(
                    "Municipios",
                    Pair(Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
                    MenuTab.MUNICIPIOS
                ),
                Triple(
                    "Tipos de Estadística",
                    Pair(Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
                    MenuTab.TIPOS_DE_ESTADISTICA
                ),
                Triple(
                    "Tipos de Instrumento",
                    Pair(Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
                    MenuTab.TIPOS_DE_INSTRUMENTO
                )
            )
        }

        DisposableEffect(Unit) {
            val dimension = Dimension(900, 640)
            window.apply {
                isResizable = true
                size = dimension
                minimumSize = dimension
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
                            title = { Text(title) },
                            actions = actions,
                            navigationIcon = {
                                IconButton(
                                    onClick = { screenModel.openNavigationDrawer() },
                                ) {
                                    Icon(Icons.Filled.Menu, contentDescription = null)
                                }
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
        items: List<Triple<String, Pair<ImageVector, ImageVector>, MenuTab>>,
        screenModel: HomeScreenModel,
        selectedTab: MenuTab,
        onChangeSelectedTab: (MenuTab) -> Unit
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
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == tab) icon.first else icon.second,
                            contentDescription = label
                        )
                    },
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
    private fun WorkArea(screenModel: HomeScreenModel, selectedTab: MenuTab, paddingValues: PaddingValues) {
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            screenModel.apply {
                when (selectedTab) {
                    MenuTab.INSTRUMENTOS -> InstrumentView()
                    MenuTab.ENTIDADES_FEDERATIVAS -> FederalEntityView()
                    MenuTab.MUNICIPIOS -> MunicipalityView()
                    MenuTab.TIPOS_DE_ESTADISTICA -> StatisticTypeView()
                    MenuTab.TIPOS_DE_INSTRUMENTO -> InstrumentTypeView()
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

    DisposableEffect(Unit) {
        current.title = title

        onDispose {
            current.title = ""
        }
    }
}

@Composable
fun HomeActions(actions: @Composable RowScope.() -> Unit) {
    val current = LocalHomeConfig.current

    DisposableEffect(Unit) {
        current.actions = actions

        onDispose {
            current.actions = @Composable {}
        }
    }
}
