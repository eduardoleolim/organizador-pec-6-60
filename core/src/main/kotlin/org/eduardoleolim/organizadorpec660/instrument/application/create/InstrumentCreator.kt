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

package org.eduardoleolim.organizadorpec660.instrument.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository
import java.util.*

class InstrumentCreator(
    private val instrumentRepository: InstrumentRepository,
    private val agencyRepository: AgencyRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun create(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String,
        file: ByteArray
    ): Either<InstrumentError, UUID> {
        if (existsAgency(agencyId).not())
            return Either.Left(AgencyNotFoundError(agencyId))

        if (existsStatisticType(statisticTypeId).not())
            return Either.Left(StatisticTypeNotFoundError(statisticTypeId))

        if (existsMunicipality(municipalityId).not())
            return Either.Left(MunicipalityNotFoundError(municipalityId))

        val existsInstrument = existsInstrument(
            statisticYear,
            statisticMonth,
            agencyId,
            statisticTypeId,
            municipalityId
        )

        if (existsInstrument)
            return Either.Left(
                InstrumentAlreadyExistsError(
                    statisticYear,
                    statisticMonth,
                    agencyId,
                    statisticTypeId,
                    municipalityId
                )
            )

        val instrumentFile = InstrumentFile.create(file)
        val instrument = Instrument.create(
            statisticYear,
            statisticMonth,
            instrumentFile.id().toString(),
            agencyId,
            statisticTypeId,
            municipalityId
        )

        instrumentRepository.save(instrument, instrumentFile)
        return Either.Right(instrument.id())
    }

    private fun existsAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencyRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) =
        StatisticTypeCriteria.idCriteria(statisticTypeId).let {
            statisticTypeRepository.count(it) > 0
        }

    private fun existsMunicipality(municipalityId: String) =
        MunicipalityCriteria.idCriteria(municipalityId).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsInstrument(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = InstrumentCriteria.otherInstrumentCriteria(
        statisticYear,
        statisticMonth,
        agencyId,
        statisticTypeId,
        municipalityId
    ).let {
        instrumentRepository.count(it) > 0
    }
}
