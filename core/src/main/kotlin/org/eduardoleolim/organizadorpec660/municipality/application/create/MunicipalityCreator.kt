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

package org.eduardoleolim.organizadorpec660.municipality.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.municipality.domain.*
import java.util.*

class MunicipalityCreator(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: FederalEntityRepository
) {
    fun create(keyCode: String, name: String, federalEntityId: String): Either<MunicipalityError, UUID> {
        try {
            if (existsFederalEntity(federalEntityId).not())
                return Either.Left(FederalEntityNotFoundError(federalEntityId))

            if (existsMunicipality(keyCode, federalEntityId))
                return Either.Left(MunicipalityAlreadyExistsError(keyCode))

            Municipality.create(keyCode, name, federalEntityId).let {
                municipalityRepository.save(it)
                return Either.Right(it.id())
            }
        } catch (e: InvalidArgumentMunicipalityException) {
            return Either.Left(CanNotSaveMunicipalityError(e))
        }
    }

    private fun existsMunicipality(keyCode: String, federalEntityId: String) =
        MunicipalityCriteria.keyCodeAndFederalEntityIdCriteria(keyCode, federalEntityId).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
