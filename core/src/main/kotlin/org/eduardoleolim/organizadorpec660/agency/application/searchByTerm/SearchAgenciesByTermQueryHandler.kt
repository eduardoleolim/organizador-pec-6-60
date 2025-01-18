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

package org.eduardoleolim.organizadorpec660.agency.application.searchByTerm

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.application.AgenciesResponse
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria

class SearchAgenciesByTermQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<AgencyError, AgenciesResponse, SearchAgenciesByTermQuery> {
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()

    override fun handle(query: SearchAgenciesByTermQuery): Either<AgencyError, AgenciesResponse> {
        val agencies = searchAgencies(query.search(), query.orders(), query.limit(), query.offset())
        val totalAgencies = countTotalAgencies(query.search())

        return agencies.map { agency ->
            val municipalityId = agency.municipalityId().toString()
            val municipality = municipalitiesCache[municipalityId] ?: searchMunicipality(municipalityId).also {
                municipalitiesCache[municipalityId] = it
            }

            val statisticTypes = agency.statisticTypeIds().map { statisticTypeId ->
                val id = statisticTypeId.value.toString()

                statisticTypesCache[id] ?: searchStatisticType(id).also { statisticTypesCache[id] = it }
            }.sortedBy { it.keyCode() }

            AgencyResponse.fromAggregate(agency, municipality, statisticTypes)
        }.let {
            municipalitiesCache.clear()
            statisticTypesCache.clear()

            Either.Right(AgenciesResponse(it, totalAgencies, query.limit(), query.offset()))
        }
    }

    private fun searchAgencies(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = AgencyCriteria.searchCriteria(
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        agencySearcher.search(it)
    }

    private fun countTotalAgencies(search: String? = null) = AgencyCriteria.searchCriteria(search).let {
        agencySearcher.count(it)
    }

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalitySearcher.search(it).first()
    }

    private fun searchStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeSearcher.search(it).first()
    }
}
