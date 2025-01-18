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

package org.eduardoleolim.organizadorpec660.statisticType.domain

sealed class StatisticTypeError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class StatisticTypeNotFoundError(val id: String) :
    StatisticTypeError("The statistic type with id <$id> was not found")

class StatisticTypeUsedInAgency : StatisticTypeError("The statistic type is used by an agency")

class StatisticTypeAlreadyExistsError(val keyCode: String) :
    StatisticTypeError("The statistic type with key code <$keyCode> already exists")

class CanNotSaveStatisticTypeError(cause: Throwable?) :
    StatisticTypeError("The statistic type could not be saved", cause)

class CanNotDeleteStatisticTypeError(cause: Throwable?) :
    StatisticTypeError("The statistic type could not be saved", cause)
