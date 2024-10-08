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

package org.eduardoleolim.organizadorpec660.statisticType.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.statisticType.domain.*

class StatisticTypeDeleter(
    private val statisticTypeRepository: StatisticTypeRepository,
    private val agencyRepository: AgencyRepository
) {
    fun delete(id: String): Either<StatisticTypeError, Unit> {
        try {
            if (exists(id).not())
                return Either.Left(StatisticTypeNotFoundError(id))

            if (usedInAgencies(id))
                return Either.Left(StatisticTypeUsedInAgency())

            statisticTypeRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Either.Left(CanNotDeleteStatisticTypeError(e))
        }
    }

    private fun exists(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun usedInAgencies(id: String) = AgencyCriteria.statisticTypeIdCriteria(id).let {
        agencyRepository.count(it) > 0
    }
}
