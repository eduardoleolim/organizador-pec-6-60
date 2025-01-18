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

package org.eduardoleolim.organizadorpec660.federalEntity.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository

class FederalEntityDeleter(
    private val federalEntityRepository: FederalEntityRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun delete(id: String): Either<FederalEntityError, Unit> {
        try {
            if (exists(id).not())
                return Either.Left(FederalEntityNotFoundError(id))

            if (hasMunicipalities(id))
                return Either.Left(FederalEntityHasMunicipalitiesError())

            federalEntityRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentFederalEntityException) {
            return Either.Left(CanNotDeleteFederalEntityError(e))
        }
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        federalEntityRepository.count(it) > 0
    }

    private fun hasMunicipalities(federalEntityId: String) =
        MunicipalityCriteria.federalEntityIdCriteria(federalEntityId).let {
            municipalityRepository.count(it) > 0
        }
}
