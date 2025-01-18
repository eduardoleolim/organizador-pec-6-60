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

package org.eduardoleolim.organizadorpec660.agency.application.searchById

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria

class SearchAgencyByIdQueryHandler(
    private val agencySearcher: AgencySearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<AgencyError, AgencyResponse, SearchAgencyByIdQuery> {
    override fun handle(query: SearchAgencyByIdQuery): Either<AgencyError, AgencyResponse> {
        val agency = searchAgency(query.id()) ?: return Either.Left(AgencyNotFoundError(query.id()))

        val municipalities = searchMunicipality(agency.municipalityId().toString())

        val statisticTypesAndInstrumentTypes = agency.statisticTypeIds().map { statisticTypeId ->
            searchStatisticType(statisticTypeId.value.toString())
        }

        return Either.Right(AgencyResponse.fromAggregate(agency, municipalities, statisticTypesAndInstrumentTypes))
    }

    private fun searchAgency(id: String) = AgencyCriteria.idCriteria(id).let {
        agencySearcher.search(it).firstOrNull()
    }

    private fun searchMunicipality(municipalityId: String): Municipality {
        val criteria = MunicipalityCriteria.idCriteria(municipalityId)

        return municipalitySearcher.search(criteria).first()
    }

    private fun searchStatisticType(statisticTypeId: String): StatisticType {
        val criteria = StatisticTypeCriteria.idCriteria(statisticTypeId)

        return statisticTypeSearcher.search(criteria).first()
    }
}
