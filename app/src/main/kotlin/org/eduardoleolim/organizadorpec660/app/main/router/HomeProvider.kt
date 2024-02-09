package org.eduardoleolim.organizadorpec660.app.main.router

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class HomeProvider : ScreenProvider {
    data object FederalEntityScreen : HomeProvider()

    data object MunicipalityScreen : HomeProvider()

    data object StatisticTypeScreen : HomeProvider()

    data object InstrumentTypeScreen : HomeProvider()

    data object InstrumentScreen : HomeProvider()
}
