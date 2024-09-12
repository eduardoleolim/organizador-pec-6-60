package org.eduardoleolim.organizadorpec660.shared.router

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.agency.views.AgencyScreen
import org.eduardoleolim.organizadorpec660.auth.views.AuthScreen
import org.eduardoleolim.organizadorpec660.federalEntity.views.FederalEntityScreen
import org.eduardoleolim.organizadorpec660.home.views.HomeScreen
import org.eduardoleolim.organizadorpec660.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.instrument.views.SaveInstrumentScreen
import org.eduardoleolim.organizadorpec660.instrument.views.ShowInstrumentDetailsScreen
import org.eduardoleolim.organizadorpec660.municipality.views.MunicipalityScreen
import org.eduardoleolim.organizadorpec660.statisticType.views.StatisticTypeScreen
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus

@Composable
fun Router(commandBus: CommandBus, queryBus: QueryBus, tempDirectory: String) {
    ScreenRegistry {
        register<MainProvider.AuthScreen> { AuthScreen(queryBus) }
        register<MainProvider.HomeScreen> { provider ->
            HomeScreen(provider.user)
        }
        register<HomeProvider.FederalEntityScreen> { FederalEntityScreen(queryBus, commandBus) }
        register<HomeProvider.MunicipalityScreen> { MunicipalityScreen(queryBus, commandBus) }
        register<HomeProvider.StatisticTypeScreen> { StatisticTypeScreen(queryBus, commandBus) }
        register<HomeProvider.InstrumentScreen> { InstrumentScreen(queryBus, commandBus, tempDirectory) }
        register<HomeProvider.SaveInstrumentScreen> { provider ->
            SaveInstrumentScreen(provider.instrumentId, queryBus, commandBus, tempDirectory)
        }
        register<HomeProvider.ShowInstrumentDetailsScreen> { provider ->
            ShowInstrumentDetailsScreen(provider.instrumentId, queryBus, tempDirectory)
        }
        register<HomeProvider.AgencyScreen> { AgencyScreen(queryBus, commandBus) }
    }

    Navigator(ScreenRegistry.get(MainProvider.AuthScreen))
}
