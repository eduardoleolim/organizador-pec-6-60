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

package org.eduardoleolim.organizadorpec660.statisticType.application.create

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.statisticType.domain.*
import java.util.*

class StatisticTypeCreator(private val statisticTypeRepository: StatisticTypeRepository) {
    fun create(keyCode: String, name: String): Either<StatisticTypeError, UUID> {
        try {
            if (existsStatisticType(keyCode))
                return Either.Left(StatisticTypeAlreadyExistsError(keyCode))

            StatisticType.create(keyCode, name).let {
                statisticTypeRepository.save(it)
                return Either.Right(it.id())
            }
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Either.Left(CanNotSaveStatisticTypeError(e))
        }
    }

    private fun existsStatisticType(keyCode: String) = StatisticTypeCriteria.keyCodeCriteria(keyCode).let {
        statisticTypeRepository.count(it) > 0
    }
}
