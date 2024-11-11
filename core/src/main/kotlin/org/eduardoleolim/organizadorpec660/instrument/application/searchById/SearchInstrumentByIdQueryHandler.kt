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

package org.eduardoleolim.organizadorpec660.instrument.application.searchById

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.application.search.AgencySearcher
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.instrument.application.DetailedInstrumentResponse
import org.eduardoleolim.organizadorpec660.instrument.application.search.InstrumentSearcher
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria

class SearchInstrumentByIdQueryHandler(
    private val instrumentSearcher: InstrumentSearcher,
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher,
    private val agencySearcher: AgencySearcher,
    private val statisticTypeSearcher: StatisticTypeSearcher
) : QueryHandler<InstrumentError, DetailedInstrumentResponse, SearchInstrumentByIdQuery> {
    override fun handle(query: SearchInstrumentByIdQuery): Either<InstrumentError, DetailedInstrumentResponse> {
        val instrumentId = query.id()
        val instrument = searchInstrument(instrumentId)
            ?: return Either.Left(InstrumentNotFoundError(instrumentId))

        val fileId = instrument.instrumentFileId().toString()
        val file = searchInstrumentFile(fileId)
            ?: return Either.Left(InstrumentFileNotFoundError(fileId))

        val municipalityId = instrument.municipalityId().toString()
        val municipality = searchMunicipality(municipalityId)
            ?: return Either.Left(MunicipalityNotFoundError(municipalityId))

        val federalEntityId = municipality.federalEntityId().toString()
        val federalEntity = searhcFederalEntity(federalEntityId)
            ?: return Either.Left(FederalEntityNotFoundError(federalEntityId))

        val agencyId = instrument.agencyId().toString()
        val agency = searchAgency(agencyId)
            ?: return Either.Left(AgencyNotFoundError(agencyId))

        val statisticTypeId = instrument.statisticTypeId().toString()
        val statisticType = searhcStatisticType(statisticTypeId)
            ?: return Either.Left(StatisticTypeNotFoundError(statisticTypeId))

        return Either.Right(
            DetailedInstrumentResponse.fromAggregate(
                instrument,
                file,
                municipality,
                federalEntity,
                agency,
                statisticType
            )
        )
    }

    private fun searchInstrument(id: String) = InstrumentCriteria.idCriteria(id).let {
        instrumentSearcher.search(it).firstOrNull()
    }

    private fun searchInstrumentFile(instrumentId: String) = instrumentSearcher.searchInstrumentFile(instrumentId)

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalitySearcher.search(it).firstOrNull()
    }

    private fun searhcFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntitySearcher.search(it).firstOrNull()
    }

    private fun searchAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencySearcher.search(it).firstOrNull()
    }

    private fun searhcStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeSearcher.search(it).firstOrNull()
    }
}
