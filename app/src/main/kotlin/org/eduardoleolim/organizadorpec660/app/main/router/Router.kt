package org.eduardoleolim.organizadorpec660.app.main.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorpec660.app.auth.AuthScreen
import org.eduardoleolim.organizadorpec660.app.federalEntity.FederalEntityScreen
import org.eduardoleolim.organizadorpec660.app.home.HomeScreen
import org.eduardoleolim.organizadorpec660.app.instrument.InstrumentScreen
import org.eduardoleolim.organizadorpec660.app.instrumentType.InstrumentTypeScreen
import org.eduardoleolim.organizadorpec660.app.municipality.MunicipalityScreen
import org.eduardoleolim.organizadorpec660.app.statisticType.StatisticTypeScreen
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryBus

@Composable
fun FrameWindowScope.Router(commandBus: CommandBus, queryBus: QueryBus) {
    ScreenRegistry {
        register<MainProvider.AuthScreen> { AuthScreen(window, queryBus) }
        register<MainProvider.HomeScreen> { HomeScreen(window, it.user) }
        register<HomeProvider.FederalEntityScreen> { FederalEntityScreen(queryBus, commandBus) }
        register<HomeProvider.MunicipalityScreen> { MunicipalityScreen(queryBus, commandBus) }
        register<HomeProvider.StatisticTypeScreen> { StatisticTypeScreen(queryBus, commandBus) }
        register<HomeProvider.InstrumentTypeScreen> { InstrumentTypeScreen(queryBus, commandBus) }
        register<HomeProvider.InstrumentScreen> { InstrumentScreen(queryBus, commandBus) }
    }

    Navigator(ScreenRegistry.get(MainProvider.AuthScreen))
}
