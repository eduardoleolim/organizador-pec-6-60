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

package org.eduardoleolim.organizadorpec660.instrument.domain

sealed class InstrumentError(override val message: String, override val cause: Throwable? = null) :
    Error(message, cause)

class InstrumentNotFoundError(key: String) : InstrumentError("The instrument with identifier <$key> was not found")

class InstrumentFileNotFoundError(key: String) :
    InstrumentError("The instrument file with identifier <$key> was not found")

class AgencyNotFoundError(val key: String) : InstrumentError("The agency with identifier <$key> was not found")

class StatisticTypeNotFoundError(val key: String) :
    InstrumentError("The statistic type with identifier <$key> was not found")

class MunicipalityNotFoundError(val key: String) :
    InstrumentError("The municipality with identifier <$key> was not found")

class FederalEntityNotFoundError(val key: String) :
    InstrumentError("The federal entity with identifier <$key> was not found")

class InstrumentFileRequiredError : InstrumentError("A new instrument must be saved with a instrument file")

class InstrumentFileFailSaveError(override val cause: Throwable? = null) :
    InstrumentError("An error occurred during saving process")

class InstrumentAlreadyExistsError(
    statisticYear: Int,
    statisticMonth: Int,
    agencyId: String,
    statisticTypeId: String,
    municipalityId: String
) : InstrumentError("The instrument with statistic year <$statisticYear>, statistic month <$statisticMonth>, agency id <$agencyId>, statistic type id <$statisticTypeId>, and municipality id <$municipalityId> already exists")

class CanNotDeleteSavedInstrumentError :
    InstrumentError("The instrument can not be deleted because its status is saved in SIRESO")

class CanNotImportInstrumentsError(val warnings: List<InstrumentImportWarning>) :
    InstrumentError("The instruments can not be imported")

class InstrumentImportFieldNotFound(val instrumentName: String, val field: InstrumentImportDataFields) :
    InstrumentError("The instrument <$instrumentName> has a missing field: ${field.value}")

data class InstrumentImportWarning(val index: Int, val error: InstrumentError)
