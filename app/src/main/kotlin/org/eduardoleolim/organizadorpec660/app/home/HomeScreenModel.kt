package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.eduardoleolim.organizadorpec660.app.auth.AuthScreen
import org.eduardoleolim.organizadorpec660.app.main.router.HomeProvider

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

    fun closeNavigationDrawer(onOpen: () -> Unit = {}, onOpened: () -> Unit = {}) {
        screenModelScope.launch(compositionContext.effectCoroutineContext) {
            onOpen()
            drawerState.close()
            onOpened()
        }
    }

    @Composable
    fun InstrumentView() {
        Navigator(ScreenRegistry.get(HomeProvider.InstrumentScreen))
    }

    @Composable
    fun FederalEntityView() {
        Navigator(ScreenRegistry.get(HomeProvider.FederalEntityScreen))
    }

    @Composable
    fun MunicipalityView() {
        Navigator(ScreenRegistry.get(HomeProvider.MunicipalityScreen))
    }

    @Composable
    fun StatisticTypeView() {
        Navigator(ScreenRegistry.get(HomeProvider.StatisticTypeScreen))
    }

    @Composable
    fun InstrumentTypeView() {
        Navigator(ScreenRegistry.get(HomeProvider.InstrumentTypeScreen))
    }

    fun logout() {
        navigator.popUntil<AuthScreen, Screen>()
    }
}
