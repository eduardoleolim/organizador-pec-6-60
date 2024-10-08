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

package org.eduardoleolim.organizadorpec660.federalEntity.application.update

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.*

class FederalEntityUpdater(private val repository: FederalEntityRepository) {
    fun update(id: String, keyCode: String, name: String): Either<FederalEntityError, Unit> {
        try {
            val federalEntity = searchFederalEntity(id) ?: return Either.Left(FederalEntityNotFoundError(id))

            if (existsAnotherSameKeyCode(id, keyCode))
                return Either.Left(FederalEntityAlreadyExistsError(keyCode))

            federalEntity.apply {
                changeKeyCode(keyCode)
                changeName(name)
            }.let {
                repository.save(it)
                return Either.Right(Unit)
            }
        } catch (e: InvalidArgumentFederalEntityException) {
            return Either.Left(CanNotSaveFederalEntityError(e))
        }
    }

    private fun searchFederalEntity(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.matching(it).firstOrNull()
    }

    private fun existsAnotherSameKeyCode(id: String, keyCode: String) =
        FederalEntityCriteria.anotherKeyCodeCriteria(id, keyCode).let {
            repository.count(it) > 0
        }
}
