package org.eduardoleolim.organizadorPec660.app.main.router

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import org.eduardoleolim.organizadorPec660.app.auth.views.AuthScreen
import org.eduardoleolim.organizadorPec660.app.federalEntity.views.FederalEntityScreen
import org.eduardoleolim.organizadorPec660.app.home.views.HomeScreen
import org.eduardoleolim.organizadorPec660.app.instrument.views.InstrumentScreen
import org.eduardoleolim.organizadorPec660.app.instrumentType.views.InstrumentTypeScreen
import org.eduardoleolim.organizadorPec660.app.municipality.views.MunicipalityScreen
import org.eduardoleolim.organizadorPec660.app.statisticType.views.StatisticTypeScreen
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryBus

@Composable
fun FrameWindowScope.Router(commandBus: CommandBus, queryBus: QueryBus) {
    ScreenRegistry {
        register<MainProvider.AuthScreen> { AuthScreen(window, queryBus) }
        register<MainProvider.HomeScreen> { provider ->
            HomeScreen(window, provider.user)
        }
        register<HomeProvider.FederalEntityScreen> { FederalEntityScreen(queryBus, commandBus) }
        register<HomeProvider.MunicipalityScreen> { MunicipalityScreen(queryBus, commandBus) }
        register<HomeProvider.StatisticTypeScreen> { StatisticTypeScreen(queryBus, commandBus) }
        register<HomeProvider.InstrumentTypeScreen> { InstrumentTypeScreen(queryBus, commandBus) }
        register<HomeProvider.InstrumentScreen> { InstrumentScreen(queryBus, commandBus) }
    }

    Navigator(ScreenRegistry.get(MainProvider.AuthScreen))
}
