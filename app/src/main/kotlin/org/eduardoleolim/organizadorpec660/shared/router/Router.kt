/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandBus
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryBus
import org.eduardoleolim.organizadorpec660.statisticType.views.StatisticTypeScreen

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
