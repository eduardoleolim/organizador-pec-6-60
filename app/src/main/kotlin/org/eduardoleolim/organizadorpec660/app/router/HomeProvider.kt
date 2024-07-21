package org.eduardoleolim.organizadorpec660.app.router

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class HomeProvider : ScreenProvider {
    data object FederalEntityScreen : HomeProvider()

    data object MunicipalityScreen : HomeProvider()

    data object StatisticTypeScreen : HomeProvider()

    data object InstrumentScreen : HomeProvider()

    data object SaveInstrumentScreen : HomeProvider()

    data object AgencyScreen : HomeProvider()
}
