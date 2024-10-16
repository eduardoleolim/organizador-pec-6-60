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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.eduardoleolim.organizadorpec660.auth.application.AuthUserResponse
import org.eduardoleolim.organizadorpec660.home.model.HomeScreenModel
import org.eduardoleolim.organizadorpec660.shared.resources.*
import org.eduardoleolim.window.LocalWindow
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

            items.forEach {
                val (label, icon, tab) = it
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == tab) icon.first else icon.second,
                            contentDescription = label
                        )
                    },
                    label = {
                        Text(label)
                    },
                    selected = selectedTab == tab,
                    onClick = { onChangeSelectedTab(tab) },
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
        var selectedTab by remember { mutableStateOf(MenuTab.INSTRUMENTS) }
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
                        .width(110.dp)
                        .padding(horizontal = 12.dp),
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 24.dp, top = 24.dp, bottom = 24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = MaterialTheme.shapes.large,
                ) {
                    AnimatedContent(targetState = selectedTab) { state ->
                        when (state) {
                            MenuTab.INSTRUMENTS -> screenModel.navigateToInstrumentView()
                            MenuTab.FEDERAL_ENTITIES -> screenModel.navigateToFederalEntityView()
                            MenuTab.MUNICIPALITIES -> screenModel.navigateToMunicipalityView()
                            MenuTab.STATISTIC_TYPES -> screenModel.navigateToStatisticTypeView()
                            MenuTab.AGENCIES -> screenModel.navigateToAgencyScreen()
                        }
                    }
                }
            }
        }
    }
}
