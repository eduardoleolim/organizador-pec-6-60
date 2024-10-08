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

sealed class MunicipalityError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class MunicipalityAlreadyExistsError(val keyCode: String) :
    MunicipalityError("The municipality with key code <$keyCode> already exists")

class MunicipalityNotFoundError(val id: String) :
    MunicipalityError("The municipality with id <$id> was not found")

class FederalEntityNotFoundError(val id: String) : MunicipalityError("The federal entity with id <$id> was not found")

class CanNotSaveMunicipalityError(cause: Throwable?) : MunicipalityError("The municipality could not be saved", cause)

class MunicipalityHasAgenciesError : MunicipalityError("There are agencies associated with the agency")

class CanNotDeleteMunicipalityError(cause: Throwable?) :
    MunicipalityError("The municipality could not be deleted", cause)
