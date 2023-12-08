package org.eduardoleolim.organizadorpec660.app.home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.auth.AuthScreen
import org.eduardoleolim.organizadorpec660.app.main.router.HomeProvider

class HomeScreenModel(
    private val navigator: Navigator
) : ScreenModel {

    @Composable
    fun navigateToFederalEntity() {
        Navigator(ScreenRegistry.get(HomeProvider.FederalEntityScreen))
    }

    @Composable
    fun navigateToMunicipality() {
        Navigator(ScreenRegistry.get(HomeProvider.MunicipalityScreen))
    }

    @Composable
    fun navigateToStatisticType() {
        Navigator(ScreenRegistry.get(HomeProvider.StatisticTypeScreen))
    }

    @Composable
    fun navigateToInstrumentType() {
        Navigator(ScreenRegistry.get(HomeProvider.InstrumentTypeScreen))
    }

    fun logout() {
        navigator.popUntil<AuthScreen, Screen>()
    }
}
