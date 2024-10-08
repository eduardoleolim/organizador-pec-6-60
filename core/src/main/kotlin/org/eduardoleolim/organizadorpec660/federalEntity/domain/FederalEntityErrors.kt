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

sealed class FederalEntityError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class FederalEntityNotFoundError(val id: String) :
    FederalEntityError("The federal entity with id <$id> was not found")

class FederalEntityHasMunicipalitiesError : FederalEntityError("The federal entity has municipalities")

class FederalEntityAlreadyExistsError(val keyCode: String) :
    FederalEntityError("The federal entity with key code <$keyCode> already exists")

class CanNotSaveFederalEntityError(cause: Throwable?) :
    FederalEntityError("The federal entity could not be saved", cause)

class CanNotDeleteFederalEntityError(cause: Throwable?) :
    FederalEntityError("The federal entity could not be saved", cause)

class CanNotImportFederalEntitiesError : FederalEntityError("The federal entities can not be imported")
