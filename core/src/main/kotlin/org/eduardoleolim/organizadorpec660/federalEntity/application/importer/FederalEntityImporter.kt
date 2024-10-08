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

package org.eduardoleolim.organizadorpec660.federalEntity.application.importer

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.*

data class FederalEntityImportWarning(val index: Int, val error: Throwable)

class FederalEntityImporter<I : FederalEntityImportInput>(
    private val repository: FederalEntityRepository,
    private val reader: FederalEntityImportReader<I>
) {
    fun import(
        input: I,
        overrideIfExists: Boolean
    ): Either<FederalEntityError, List<FederalEntityImportWarning>> {
        val warnings = mutableListOf<FederalEntityImportWarning>()
        val federalEntities = reader.read(input)

        federalEntities.forEachIndexed { index, federalEntityData ->
            try {
                val keyCode = federalEntityData.keyCode()
                val name = federalEntityData.name()
                val federalEntity = searchFederalEntity(keyCode)

                when {
                    federalEntity != null && overrideIfExists -> {
                        federalEntity.apply {
                            changeName(name)
                            changeKeyCode(keyCode)
                        }.let {
                            repository.save(it)
                        }
                    }

                    federalEntity == null -> {
                        FederalEntity.create(keyCode, name).let {
                            repository.save(it)
                        }
                    }
                }
            } catch (e: Throwable) {
                warnings.add(FederalEntityImportWarning(index, e))
            }
        }

        if (warnings.size == federalEntities.size) {
            return Either.Left(CanNotImportFederalEntitiesError())
        }

        return Either.Right(warnings)
    }

    private fun searchFederalEntity(keyCode: String) = FederalEntityCriteria.keyCodeCriteria(keyCode).let {
        repository.matching(it).firstOrNull()
    }
}
