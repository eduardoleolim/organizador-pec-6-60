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

import java.util.*

sealed class InvalidArgumentStatisticTypeException(
    override val message: String,
    override val cause: Throwable? = null
) :
    IllegalArgumentException(message, cause)

class InvalidStatisticTypeIdError(val id: String, override val cause: Throwable?) :
    InvalidArgumentStatisticTypeException("The id <$id> is not a valid statistic type id", cause)

class InvalidStatisticTypeKeyCodeError(val keyCode: String) :
    InvalidArgumentStatisticTypeException("The key code <$keyCode> is not a valid statistic type key code")

class InvalidStatisticTypeNameError(val name: String) :
    InvalidArgumentStatisticTypeException("The name <$name> is not a valid statistic type name")

class InvalidStatisticTypeUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentStatisticTypeException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

