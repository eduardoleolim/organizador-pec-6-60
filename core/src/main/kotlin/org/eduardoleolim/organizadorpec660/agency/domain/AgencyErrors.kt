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

package org.eduardoleolim.organizadorpec660.agency.domain

sealed class AgencyError(override val message: String, override val cause: Throwable? = null) : Error(message, cause)

class AgencyAlreadyExistsError(consecutive: String) :
    AgencyError("The agency with consecutive <$consecutive> already exists")

class AgencyNotFoundError(val id: String) : AgencyError("The agency with id <$id> was not found")

class AgencyHasNoStatisticTypesError : AgencyError("The agency must be related with at least one statistic type")

class AgencyHasInstrumentsError : AgencyError("There are instruments related to the agency")

class MunicipalityNotFoundError(val id: String) : AgencyError("The municipality with id <$id> was not found")

class StatisticTypeNotFoundError(val id: String) : AgencyError("The municipality with id <$id> was not found")

class CanNotSaveAgencyError(cause: Throwable?) : AgencyError("The agency could not be saved", cause)

class CanNotDeleteAgencyError(cause: Throwable?) : AgencyError("The agency could not be deleted", cause)
