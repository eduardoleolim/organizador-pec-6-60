/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.home.views

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.FadeTransition
import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.home.model.HomeScreenModel
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.organizadorpec660.shared.router.HomeProvider
import org.eduardoleolim.window.LocalWindow
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

enum class AppDestinations {
    INSTRUMENTS,
    FEDERAL_ENTITIES,
    MUNICIPALITIES,
    STATISTIC_TYPES,
    AGENCIES
}

sealed class NotificationBadgeType {
    data object None : NotificationBadgeType()
    data class Count(val count: Int = 0) : NotificationBadgeType()
}

data class NavigationItem(
    val tab: AppDestinations,
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    var notificationBadge: NotificationBadgeType = NotificationBadgeType.None,
)

class HomeScreen(private val user: AuthUserResponse) : Screen {
    private val items: List<NavigationItem>
        @Composable get() = listOf(
            NavigationItem(
                AppDestinations.INSTRUMENTS,
                stringResource(Res.string.instruments),
                Icons.Outlined.Description,
                Icons.Filled.Description,
                NotificationBadgeType.None
            ),
            NavigationItem(
                AppDestinations.FEDERAL_ENTITIES,
                stringResource(Res.string.federal_entities),
                Icons.AutoMirrored.Outlined.ListAlt,
                Icons.AutoMirrored.Filled.ListAlt,
                NotificationBadgeType.None
            ),
            NavigationItem(
                AppDestinations.MUNICIPALITIES,
                stringResource(Res.string.municipalities),
                Icons.AutoMirrored.Outlined.ListAlt,
                Icons.AutoMirrored.Filled.ListAlt,
                NotificationBadgeType.None
            ),
            NavigationItem(
                AppDestinations.STATISTIC_TYPES,
                stringResource(Res.string.statistic_types),
                Icons.Outlined.BarChart,
                Icons.Filled.BarChart,
                NotificationBadgeType.None
            ),
            NavigationItem(
                AppDestinations.AGENCIES,
                stringResource(Res.string.agencies),
                Icons.Outlined.Apartment,
                Icons.Filled.Apartment,
                NotificationBadgeType.None
            )
        )

    @Composable
    private fun ModalNavigationDrawerContent(
        items: List<NavigationItem>,
        screenModel: HomeScreenModel,
        selectedTab: AppDestinations,
        onSelectedTabChange: (AppDestinations) -> Unit
    ) {
        ModalDrawerSheet {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

            items.forEach { item ->
                val (tab, label, unselectedIcon, selectedIcon, notificationBadge) = item

                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == tab) selectedIcon else unselectedIcon,
                            contentDescription = label
                        )
                    },
                    label = {
                        Text(label)
                    },
                    badge = when {
                        notificationBadge is NotificationBadgeType.Count -> {
                            if (notificationBadge.count > 0) {
                                { Text(notificationBadge.count.toString()) }
                            } else {
                                { Badge() }
                            }
                        }

                        else -> null
                    },
                    selected = selectedTab == tab,
                    onClick = { onSelectedTabChange(tab) },
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(Modifier.weight(1.0f))

            HorizontalDivider()

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "logout"
                    )
                },
                label = {
                    Text(stringResource(Res.string.logout))
                },
                selected = false,
                onClick = { screenModel.logout() },
                shape = MaterialTheme.shapes.medium
            )
        }
    }

    @Composable
    private fun NavigationRailContent(
        items: List<NavigationItem>,
        screenModel: HomeScreenModel,
        selectedTab: AppDestinations,
        onChangeSelectedTab: (AppDestinations) -> Unit
    ) {
        items.forEach { item ->
            val (tab, label, unselectedIcon, selectedIcon, notificationBadge) = item

            NavigationRailItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (notificationBadge is NotificationBadgeType.Count) {
                                Badge(
                                    content = notificationBadge.takeIf { it.count > 0 }?.let {
                                        { Text(it.count.toString()) }
                                    }
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selectedTab == tab) selectedIcon else unselectedIcon,
                            contentDescription = label
                        )
                    }
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
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "logout"
                )
            },
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
        var selectedTab by remember { mutableStateOf(AppDestinations.INSTRUMENTS) }
        val density = LocalDensity.current

        LaunchedEffect(Unit) {
            val dimension = with(density) { Dimension(900.dp.roundToPx(), 640.dp.roundToPx()) }
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
                    onSelectedTabChange = {
                        screenModel.closeNavigationDrawer(
                            onClosed = { selectedTab = it }
                        )
                    }
                )
            },
        ) {
            Row {
                NavigationRail(
                    modifier = Modifier.width(110.dp),
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

                Box(
                    modifier = Modifier.padding(start = 0.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        AnimatedContent(targetState = selectedTab) { state ->
                            val screenProvider = when (state) {
                                AppDestinations.INSTRUMENTS -> HomeProvider.InstrumentScreen
                                AppDestinations.FEDERAL_ENTITIES -> HomeProvider.FederalEntityScreen
                                AppDestinations.MUNICIPALITIES -> HomeProvider.MunicipalityScreen
                                AppDestinations.STATISTIC_TYPES -> HomeProvider.StatisticTypeScreen
                                AppDestinations.AGENCIES -> HomeProvider.AgencyScreen
                            }

                            Navigator(ScreenRegistry.get(screenProvider)) { navigator ->
                                FadeTransition(navigator)
                            }
                        }
                    }
                }
            }
        }
    }
}
