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

package org.eduardoleolim.organizadorpec660.instrument.data

sealed class InvalidInstrumentArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyInstrumentDataException(
    val isStatisticYearUnselected: Boolean,
    val isStatisticMonthUnselected: Boolean,
    val isMunicipalityUnselected: Boolean,
    val isAgencyUnselected: Boolean,
    val isStatisticTypeUnselected: Boolean,
    val isInstrumentFileUnselected: Boolean,
) : InvalidInstrumentArgumentException(
    when {
        isStatisticYearUnselected && isStatisticMonthUnselected && isMunicipalityUnselected && isAgencyUnselected && isStatisticTypeUnselected && isInstrumentFileUnselected ->
            "The year, month, municipality, agency, statistic type, and instrument file are required"

        isStatisticYearUnselected && isStatisticMonthUnselected -> "The year and month are required"
        isMunicipalityUnselected && isAgencyUnselected -> "The municipality and agency are required"
        isStatisticTypeUnselected && isInstrumentFileUnselected -> "The statistic type and instrument file are required"
        isStatisticYearUnselected -> "The year is required"
        isStatisticMonthUnselected -> "The month is required"
        isMunicipalityUnselected -> "The municipality is required"
        isAgencyUnselected -> "The agency is required"
        isStatisticTypeUnselected -> "The statistic type is required"
        isInstrumentFileUnselected -> "The instrument file is required"
        else -> "No data is missing"
    }
)
