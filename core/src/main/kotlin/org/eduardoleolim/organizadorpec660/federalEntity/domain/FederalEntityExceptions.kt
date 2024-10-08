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

package org.eduardoleolim.organizadorpec660.federalEntity.domain

import java.util.*

sealed class InvalidArgumentFederalEntityException(
    override val message: String,
    override val cause: Throwable? = null
) :
    IllegalArgumentException(message, cause)

class InvalidFederalEntityIdError(val id: String, override val cause: Throwable?) :
    InvalidArgumentFederalEntityException("The id <$id> is not a valid federal entity id", cause)

class InvalidFederalEntityKeyCodeError(val keyCode: String) :
    InvalidArgumentFederalEntityException("The key code <$keyCode> is not a valid federal entity key code")

class InvalidFederalEntityNameError(val name: String) :
    InvalidArgumentFederalEntityException("The name <$name> is not a valid federal entity name")

class InvalidFederalEntityUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentFederalEntityException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")
