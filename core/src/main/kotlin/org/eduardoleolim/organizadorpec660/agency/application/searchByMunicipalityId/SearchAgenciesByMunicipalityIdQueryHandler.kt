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

package org.eduardoleolim.organizadorpec660.agency.application.searchByMunicipalityId

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.MunicipalityAgenciesResponse
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

class SearchAgenciesByMunicipalityIdQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<AgencyError, MunicipalityAgenciesResponse, SearchAgenciesByMunicipalityIdQuery> {
    private val municipalitiesCache = mutableMapOf<String, Municipality>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()

    override fun handle(query: SearchAgenciesByMunicipalityIdQuery): Either<AgencyError, MunicipalityAgenciesResponse> {
        val agencies = searchAgencies(query.municipalityId())

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

            Either.Right(MunicipalityAgenciesResponse(it))
        }
    }

    private fun searchAgencies(municipalityId: String) = AgencyCriteria.municipalityIdCriteria(municipalityId).let {
        agencySearcher.search(it)
    }

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalitySearcher.search(it).first()
    }

    private fun searchStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeSearcher.search(it).first()
    }
}
