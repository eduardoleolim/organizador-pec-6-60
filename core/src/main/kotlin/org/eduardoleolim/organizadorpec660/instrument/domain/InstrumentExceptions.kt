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

package org.eduardoleolim.organizadorpec660.instrument.domain

import java.util.*

sealed class InvalidInstrumentException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidInstrumentIdError(val id: String, override val cause: Throwable?) :
    InvalidInstrumentException("The id <$id> is not a valid instrument id", cause)

class InvalidInstrumentStatisticYearError(year: Int) :
    InvalidInstrumentException("The year <$year> is not a valid instrument statistic year")

class InvalidInstrumentStatisticMonthError(month: Int) :
    InvalidInstrumentException("The month <$month> is not a valid instrument statistic month. It must be between 1 and 12")

class InvalidInstrumentConsecutiveError(consecutive: String) :
    InvalidInstrumentException("The consecutive <$consecutive> is not a valid instrument consecutive")

class InvalidInstrumentFileIdError(id: String, override val cause: Throwable?) :
    InvalidInstrumentException("The id <$id> is not a valid instrument file id", cause)

class InvalidInstrumentUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidInstrumentException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class InvalidEmptyInstrumentContentError :
    InvalidInstrumentException("The instrument content cannot be empty")

class InvalidInstrumentContentError :
    InvalidInstrumentException("The instrument content is not a valid PDF file")
