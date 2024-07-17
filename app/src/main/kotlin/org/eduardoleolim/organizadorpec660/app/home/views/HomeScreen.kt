package org.eduardoleolim.organizadorpec660.app.home.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.app.generated.resources.*
import org.eduardoleolim.organizadorpec660.app.home.model.HomeScreenModel
import org.eduardoleolim.organizadorpec660.app.window.LocalWindow
import org.eduardoleolim.organizadorpec660.core.auth.application.AuthUserResponse
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

enum class MenuTab {
    INSTRUMENTS,
    FEDERAL_ENTITIES,
    MUNICIPALITIES,
    STATISTIC_TYPES,
    AGENCIES
}

class HomeScreen(private val user: AuthUserResponse) : Screen {
    private val items: List<Triple<String, Pair<ImageVector, ImageVector>, MenuTab>>
        @Composable get() = listOf(
            Triple(
                stringResource(Res.string.instruments),
                Pair(Icons.Filled.Description, Icons.Outlined.Description),
                MenuTab.INSTRUMENTS
            ),
            Triple(
                stringResource(Res.string.federal_entities),
                Pair(Icons.AutoMirrored.Filled.ListAlt, Icons.AutoMirrored.Outlined.ListAlt),
                MenuTab.FEDERAL_ENTITIES
            ),
            Triple(
                stringResource(Res.string.municipalities),
                Pair(Icons.AutoMirrored.Filled.ListAlt, Icons.AutoMirrored.Outlined.ListAlt),
                MenuTab.MUNICIPALITIES
            ),
            Triple(
                stringResource(Res.string.statistic_types),
                Pair(Icons.Filled.BarChart, Icons.Outlined.BarChart),
                MenuTab.STATISTIC_TYPES
            ),
            Triple(
                stringResource(Res.string.agencies),
                Pair(Icons.Filled.Apartment, Icons.Outlined.Apartment),
                MenuTab.AGENCIES
            )
        )

    @Composable
    private fun ModalNavigationDrawerContent(
        items: List<Triple<String, Pair<ImageVector, ImageVector>, MenuTab>>,
        screenModel: HomeScreenModel,
        selectedTab: MenuTab,
        onChangeSelectedTab: (MenuTab) -> Unit
    ) {
        ModalDrawerSheet {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.app_name))

                Spacer(Modifier.weight(1.0f))

                IconButton(
                    onClick = { screenModel.closeNavigationDrawer() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = "Menu opened"
                    )
                }
            }

            HorizontalDivider()

            items.forEach {
                val (label, icon, tab) = it
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == tab) icon.first else icon.second,
                            contentDescription = label
                        )
                    },
                    label = { Text(label) },
                    selected = selectedTab == tab,
                    onClick = { onChangeSelectedTab(tab) },
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(Modifier.weight(1.0f))

            HorizontalDivider()

            NavigationDrawerItem(
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "logout") },
                label = { Text(stringResource(Res.string.logout)) },
                selected = false,
                onClick = { screenModel.logout() },
                shape = MaterialTheme.shapes.medium
            )
        }
    }

    @Composable
    private fun NavigationRailContent(
        items: List<Triple<String, Pair<ImageVector, ImageVector>, MenuTab>>,
        screenModel: HomeScreenModel,
        selectedTab: MenuTab,
        onChangeSelectedTab: (MenuTab) -> Unit
    ) {
        items.forEach { item ->
            val (label, icon, tab) = item
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = if (selectedTab == tab) icon.first else icon.second,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        textAlign = TextAlign.Center
                    )
                },
                selected = selectedTab == tab,
                onClick = { onChangeSelectedTab(tab) },
            )
        }

        NavigationRailItem(
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "logout") },
            label = {
                Text(
                    text = stringResource(Res.string.logout),
                    textAlign = TextAlign.Center
                )
            },
            selected = false,
            onClick = { screenModel.logout() },
        )
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val window = LocalWindow.current
        val compositionContext = rememberCompositionContext()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val screenModel = rememberScreenModel { HomeScreenModel(navigator, drawerState, compositionContext) }
        var selectedTab by remember { mutableStateOf(MenuTab.INSTRUMENTS) }

        LaunchedEffect(Unit) {
            val dimension = Dimension(900, 640)
            window.apply {
                isResizable = true
                size = dimension
                minimumSize = dimension
                setLocationRelativeTo(null)
            }
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalNavigationDrawerContent(
                    items = items,
                    screenModel = screenModel,
                    selectedTab = selectedTab,
                    onChangeSelectedTab = {
                        screenModel.closeNavigationDrawer(
                            onClosed = { selectedTab = it }
                        )
                    }
                )
            },
        ) {
            Row {
                NavigationRail(
                    modifier = Modifier
                        .width(116.dp)
                        .padding(horizontal = 16.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    header = {
                        Spacer(Modifier.height(24.dp))

                        IconButton(
                            onClick = { screenModel.openNavigationDrawer() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Open menu"
                            )
                        }
                    }
                ) {
                    NavigationRailContent(
                        items = items,
                        screenModel = screenModel,
                        selectedTab = selectedTab,
                        onChangeSelectedTab = {
                            screenModel.closeNavigationDrawer(onClosed = { selectedTab = it })
                        }
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                        .padding(start = 8.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.large,
                ) {
                    AnimatedContent(targetState = selectedTab) { targetState ->
                        screenModel.apply {
                            when (targetState) {
                                MenuTab.INSTRUMENTS -> InstrumentView()
                                MenuTab.FEDERAL_ENTITIES -> FederalEntityView()
                                MenuTab.MUNICIPALITIES -> MunicipalityView()
                                MenuTab.STATISTIC_TYPES -> StatisticTypeView()
                                MenuTab.AGENCIES -> AgencyScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
