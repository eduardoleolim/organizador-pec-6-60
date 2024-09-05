package org.eduardoleolim.organizadorpec660.app.shared.router

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class HomeProvider : ScreenProvider {
    data object FederalEntityScreen : HomeProvider()

    data object MunicipalityScreen : HomeProvider()

    data object StatisticTypeScreen : HomeProvider()

    data object InstrumentScreen : HomeProvider()

    data class SaveInstrumentScreen(val instrumentId: String?) : HomeProvider()

    data class ShowInstrumentDetailsScreen(val instrumentId: String) : HomeProvider()

    data object AgencyScreen : HomeProvider()
}
