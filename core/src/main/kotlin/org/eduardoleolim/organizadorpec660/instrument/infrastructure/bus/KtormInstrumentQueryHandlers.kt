/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.instrument.infrastructure.bus

import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.instrument.application.searchById.SearchInstrumentByIdQuery
import org.eduardoleolim.organizadorpec660.instrument.application.searchById.SearchInstrumentByIdQueryHandler
import org.eduardoleolim.organizadorpec660.instrument.application.searchByTerm.SearchInstrumentsByTermQuery
import org.eduardoleolim.organizadorpec660.instrument.application.searchByTerm.SearchInstrumentsByTermQueryHandler
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormInstrumentQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query<*, *>>, QueryHandler<*, *, out Query<*, *>>> = mapOf(
        SearchInstrumentByIdQuery::class to searchByIdQueryHandler(),
        SearchInstrumentsByTermQuery::class to searchByTermQueryHandler()
    )

    private fun searchByIdQueryHandler(): QueryHandler<*, *, out Query<*, *>> {
        val instrumentSearcher: InstrumentSearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val agencySearcher: AgencySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val queryHandler = SearchInstrumentByIdQueryHandler(
            instrumentSearcher,
            municipalitySearcher,
            federalEntitySearcher,
            agencySearcher,
            statisticTypeSearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByTermQueryHandler(): QueryHandler<*, *, out Query<*, *>> {
        val instrumentSearcher: InstrumentSearcher by inject()
        val agencySearcher: AgencySearcher by inject()
        val statisticTypeSearcher: StatisticTypeSearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val municipalitySearcher: MunicipalitySearcher by inject()

        val queryHandler = SearchInstrumentsByTermQueryHandler(
            instrumentSearcher,
            agencySearcher,
            statisticTypeSearcher,
            federalEntitySearcher,
            municipalitySearcher
        )

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
