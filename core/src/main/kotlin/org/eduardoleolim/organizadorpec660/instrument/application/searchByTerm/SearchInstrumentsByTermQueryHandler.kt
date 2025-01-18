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

package org.eduardoleolim.organizadorpec660.instrument.application.searchByTerm

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.InstrumentsResponse
import org.eduardoleolim.organizadorpec660.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria

class SearchInstrumentsByTermQueryHandler(
    private val instrumentSearcher: InstrumentSearcher,
    private val agencySearcher: AgencySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher,
    private val federalEntitySearcher: FederalEntitySearcher,
    private val municipalitySearcher: MunicipalitySearcher
) : QueryHandler<InstrumentError, InstrumentsResponse, SearchInstrumentsByTermQuery> {
    private val agenciesCache = mutableMapOf<String, Agency>()
    private val statisticTypesCache = mutableMapOf<String, StatisticType>()
    private val federalEntitiesCache = mutableMapOf<String, FederalEntity>()
    private val municipalitiesCache = mutableMapOf<String, Municipality>()

    override fun handle(query: SearchInstrumentsByTermQuery): Either<InstrumentError, InstrumentsResponse> {
        val instruments = searchInstruments(
            query.agencyId(),
            query.statisticTypeId(),
            query.federalEntityId(),
            query.municipalityId(),
            query.year(),
            query.month(),
            query.search(),
            query.orders(),
            query.limit(),
            query.offset()
        )
        val totalInstruments = countTotalInstruments(
            query.agencyId(),
            query.statisticTypeId(),
            query.federalEntityId(),
            query.municipalityId(),
            query.year(),
            query.month(),
            query.search()
        )

        return instruments.map { instrument ->
            val agencyId = instrument.agencyId().toString()
            val agency = agenciesCache[agencyId] ?: searchAgency(agencyId).also {
                agenciesCache[agencyId] = it
            }

            val statisticTypeId = instrument.statisticTypeId().toString()
            val statisticType = statisticTypesCache[statisticTypeId] ?: searchStatisticType(statisticTypeId).also {
                statisticTypesCache[statisticTypeId] = it
            }

            val municipalityId = instrument.municipalityId().toString()
            val municipality = municipalitiesCache[municipalityId] ?: searchMunicipality(municipalityId).also {
                municipalitiesCache[municipalityId] = it
            }

            val federalEntityId = municipality.federalEntityId().toString()
            val federalEntity = federalEntitiesCache[federalEntityId] ?: searchFederalEntity(federalEntityId).also {
                federalEntitiesCache[federalEntityId] = it
            }

            InstrumentResponse.fromAggregate(instrument, agency, statisticType, federalEntity, municipality)
        }.let {
            agenciesCache.clear()
            statisticTypesCache.clear()
            federalEntitiesCache.clear()
            municipalitiesCache.clear()

            Either.Right(InstrumentsResponse(it, totalInstruments, query.limit(), query.offset()))
        }
    }

    private fun searchInstruments(
        agencyId: String?,
        statisticTypeId: String?,
        federalEntityId: String?,
        municipalityId: String?,
        year: Int?,
        month: Int?,
        search: String?,
        orders: Array<HashMap<String, String>>?,
        limit: Int?,
        offset: Int?
    ) = InstrumentCriteria.searchCriteria(
        agencyId = agencyId,
        statisticTypeId = statisticTypeId,
        federalEntityId = federalEntityId,
        municipalityId = municipalityId,
        year = year,
        month = month,
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        instrumentSearcher.search(it)
    }

    private fun countTotalInstruments(
        agencyId: String?,
        statisticTypeId: String?,
        federalEntityId: String?,
        municipalityId: String?,
        year: Int?,
        month: Int?,
        search: String?
    ) = InstrumentCriteria.searchCriteria(
        agencyId = agencyId,
        statisticTypeId = statisticTypeId,
        federalEntityId = federalEntityId,
        municipalityId = municipalityId,
        year = year,
        month = month,
        search = search
    ).let {
        instrumentSearcher.count(it)
    }

    private fun searchAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencySearcher.search(it).first()
    }

    private fun searchStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeSearcher.search(it).first()
    }

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalitySearcher.search(it).first()
    }

    private fun searchFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntitySearcher.search(it).first()
    }
}
