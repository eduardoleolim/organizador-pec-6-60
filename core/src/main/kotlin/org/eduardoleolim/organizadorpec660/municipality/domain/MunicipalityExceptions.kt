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

package org.eduardoleolim.organizadorpec660.municipality.domain

import java.util.*

sealed class InvalidArgumentMunicipalityException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidMunicipalityIdError(val id: String, override val cause: Throwable?) :
    InvalidArgumentMunicipalityException("The id <$id> is not a valid municipality id", cause)

class InvalidMunicipalityKeyCodeError(val keyCode: String) :
    InvalidArgumentMunicipalityException("The key code <$keyCode> is not a valid municipality key code")

class InvalidMunicipalityUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentMunicipalityException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class InvalidMunicipalityNameError(val name: String) :
    InvalidArgumentMunicipalityException("The name <$name> is not a valid municipality name")
