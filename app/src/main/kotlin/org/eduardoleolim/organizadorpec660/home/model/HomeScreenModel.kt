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

package org.eduardoleolim.organizadorpec660.home.model

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.auth.views.AuthScreen
import org.eduardoleolim.organizadorpec660.shared.router.HomeProvider

class HomeScreenModel(
    private val navigator: Navigator,
    private val drawerState: DrawerState,
    private val compositionContext: CompositionContext
) : ScreenModel {
    fun openNavigationDrawer(onOpen: () -> Unit = {}, onOpened: () -> Unit = {}) {
        screenModelScope.launch(compositionContext.effectCoroutineContext) {
            onOpen()
            drawerState.open()
            onOpened()
        }
    }

    fun closeNavigationDrawer(onClose: () -> Unit = {}, onClosed: () -> Unit = {}) {
        screenModelScope.launch(compositionContext.effectCoroutineContext) {
            onClose()
            drawerState.close()
            onClosed()
        }
    }

    @Composable
    fun navigateToInstrumentView() {
        Navigator(ScreenRegistry.get(HomeProvider.InstrumentScreen)) { navigator ->
            FadeTransition(navigator)
        }
    }

    @Composable
    fun navigateToFederalEntityView() {
        Navigator(ScreenRegistry.get(HomeProvider.FederalEntityScreen)) { navigator ->
            FadeTransition(navigator)
        }
    }

    @Composable
    fun navigateToMunicipalityView() {
        Navigator(ScreenRegistry.get(HomeProvider.MunicipalityScreen)) { navigator ->
            FadeTransition(navigator)
        }
    }

    @Composable
    fun navigateToStatisticTypeView() {
        Navigator(ScreenRegistry.get(HomeProvider.StatisticTypeScreen)) { navigator ->
            FadeTransition(navigator)
        }
    }

    @Composable
    fun navigateToAgencyScreen() {
        Navigator(ScreenRegistry.get(HomeProvider.AgencyScreen)) { navigator ->
            FadeTransition(navigator)
        }
    }

    fun logout() {
        navigator.popUntil<AuthScreen, Screen>()
    }
}
