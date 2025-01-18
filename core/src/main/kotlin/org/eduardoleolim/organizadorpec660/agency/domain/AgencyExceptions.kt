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

package org.eduardoleolim.organizadorpec660.agency.domain

import java.util.*

sealed class InvalidArgumentAgencyException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidAgencyIdException(val id: String, override val cause: Throwable?) :
    InvalidArgumentAgencyException("The id <$id> is not a valid agency id", cause)

class InvalidAgencyNameException(val name: String) :
    InvalidArgumentAgencyException("The name <$name> is not a valid agency name")

class InvalidAgencyConsecutiveException(val consecutive: String) :
    InvalidArgumentAgencyException("The consecutive <$consecutive> is not a valid agency consecutive")

class InvalidAgencyStatisticTypesException :
    InvalidArgumentAgencyException("The agency must to have at least a statistic type association")

class InvalidAgencyUpdateDateException(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentAgencyException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

