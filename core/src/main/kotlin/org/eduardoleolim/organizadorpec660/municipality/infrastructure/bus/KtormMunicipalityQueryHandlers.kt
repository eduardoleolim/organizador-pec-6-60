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

package org.eduardoleolim.organizadorpec660.municipality.infrastructure.bus

import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.application.searchById.SearchMunicipalityByIdQuery
import org.eduardoleolim.organizadorpec660.municipality.application.searchById.SearchMunicipalityByIdQueryHandler
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQuery
import org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm.SearchMunicipalitiesByTermQueryHandler
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.shared.infrastructure.bus.KtormQueryHandlerDecorator
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinComponent
import org.eduardoleolim.organizadorpec660.shared.infrastructure.koin.KtormAppKoinContext
import org.koin.core.component.inject
import org.ktorm.database.Database
import kotlin.reflect.KClass

class KtormMunicipalityQueryHandlers(context: KtormAppKoinContext) : KtormAppKoinComponent(context) {
    private val database: Database by inject()

    val handlers: Map<KClass<out Query<*, *>>, QueryHandler<*, *, out Query<*, *>>> = mapOf(
        SearchMunicipalitiesByTermQuery::class to searchByTermQueryHandler(),
        SearchMunicipalityByIdQuery::class to searchByIdQueryHandler()
    )

    private fun searchByTermQueryHandler(): QueryHandler<*, *, out Query<*, *>> {
        val municipalitySearcher: MunicipalitySearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val queryHandler = SearchMunicipalitiesByTermQueryHandler(municipalitySearcher, federalEntitySearcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }

    private fun searchByIdQueryHandler(): QueryHandler<*, *, out Query<*, *>> {
        val municipalitySearcher: MunicipalitySearcher by inject()
        val federalEntitySearcher: FederalEntitySearcher by inject()
        val queryHandler = SearchMunicipalityByIdQueryHandler(municipalitySearcher, federalEntitySearcher)

        return KtormQueryHandlerDecorator(database, queryHandler)
    }
}
