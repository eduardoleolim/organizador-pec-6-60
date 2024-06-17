package org.eduardoleolim.organizadorpec660.app.router

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.agency.views.AgencyScreen
import org.eduardoleolim.organizadorpec660.app.auth.views.AuthScreen
import org.eduardoleolim.organizadorpec660.app.federalEntity.views.FederalEntityScreen
import org.eduardoleolim.organizadorpec660.app.home.views.HomeScreen
import org.eduardoleolim.organizadorpec660.app.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorpec660.app.municipality.views.MunicipalityScreen
import org.eduardoleolim.organizadorpec660.app.statisticType.views.StatisticTypeScreen
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

@Composable
fun Router(commandBus: CommandBus, queryBus: QueryBus) {
    ScreenRegistry {
        register<MainProvider.AuthScreen> { AuthScreen(queryBus) }
        register<MainProvider.HomeScreen> { provider ->
            HomeScreen(provider.user)
        }
        register<HomeProvider.FederalEntityScreen> { FederalEntityScreen(queryBus, commandBus) }
        register<HomeProvider.MunicipalityScreen> { MunicipalityScreen(queryBus, commandBus) }
        register<HomeProvider.StatisticTypeScreen> { StatisticTypeScreen(queryBus, commandBus) }
        register<HomeProvider.InstrumentScreen> { InstrumentScreen(queryBus, commandBus) }
        register<HomeProvider.AgencyScreen> { AgencyScreen(queryBus, commandBus) }
    }

    Navigator(ScreenRegistry.get(MainProvider.AuthScreen))
}
