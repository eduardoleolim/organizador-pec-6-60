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

package org.eduardoleolim.organizadorpec660.agency.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository
import java.util.*

class AgencyCreator(
    private val agencyRepository: AgencyRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val statisticTypeRepository: StatisticTypeRepository
) {
    fun create(
        name: String,
        consecutive: String,
        municipalityId: String,
        statisticTypeIds: List<String>
    ): Either<AgencyError, UUID> {
        try {
            if (existsMunicipality(municipalityId).not())
                return Either.Left(MunicipalityNotFoundError(municipalityId))

            if (statisticTypeIds.isEmpty())
                return Either.Left(AgencyHasNoStatisticTypesError())

            statisticTypeIds.forEach { statisticTypeId ->
                if (existsStatisticType(statisticTypeId).not())
                    return Either.Left(StatisticTypeNotFoundError(statisticTypeId))
            }

            if (existsAnotherAgencySameConsecutive(consecutive, municipalityId))
                return Either.Left(AgencyAlreadyExistsError(consecutive))

            return Agency.create(name, consecutive, municipalityId, statisticTypeIds).let {
                agencyRepository.save(it)
                Either.Right(it.id())
            }
        } catch (e: InvalidArgumentAgencyException) {
            return Either.Left(CanNotSaveAgencyError(e))
        }
    }

    private fun existsMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun existsAnotherAgencySameConsecutive(consecutive: String, municipalityId: String) =
        AgencyCriteria.anotherConsecutiveCriteria(consecutive, municipalityId).let {
            agencyRepository.count(it) > 0
        }
}
