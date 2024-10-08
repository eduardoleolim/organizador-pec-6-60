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

package org.eduardoleolim.organizadorpec660.agency.data

sealed class InvalidAgencyArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyAgencyDataException(
    val isNameEmpty: Boolean,
    val isConsecutiveEmpty: Boolean,
    val isMunicipalityEmpty: Boolean,
    val isStatisticTypesEmpty: Boolean
) : InvalidAgencyArgumentException(
    when {
        isNameEmpty && isConsecutiveEmpty && isMunicipalityEmpty && isStatisticTypesEmpty -> "El nombre, el consecutivo, el municipio y al menos un tipo de estadística son requeridos"

        isNameEmpty && isConsecutiveEmpty && isMunicipalityEmpty -> "El nombre, el consecutivo y el municipio son requeridos"
        isNameEmpty && isConsecutiveEmpty && isStatisticTypesEmpty -> "El nombre, el consecutivo y al menos un tipo de estadística son requeridos"
        isNameEmpty && isMunicipalityEmpty && isStatisticTypesEmpty -> "El nombre, el municipio y al menos un tipo de estadística son requeridos"
        isConsecutiveEmpty && isMunicipalityEmpty && isStatisticTypesEmpty -> "El consecutivo, el municipio y al menos un tipo de estadística son requeridos"

        isNameEmpty && isConsecutiveEmpty -> "El nombre y el consecutivo son requeridos"
        isNameEmpty && isMunicipalityEmpty -> "El nombre y el municipio son requeridos"
        isNameEmpty && isStatisticTypesEmpty -> "El nombre y al menos un tipo de estadística son requeridos"
        isConsecutiveEmpty && isMunicipalityEmpty -> "El consecutivo y el municipio son requeridos"
        isConsecutiveEmpty && isStatisticTypesEmpty -> "El consecutivo y al menos un tipo de estadística son requeridos"
        isMunicipalityEmpty && isStatisticTypesEmpty -> "El municipio y al menos un tipo de estadística son requeridos"

        isNameEmpty -> "El nombre es requerido"
        isConsecutiveEmpty -> "El consecutivo es requerido"
        isMunicipalityEmpty -> "El municipio es requerido"
        isStatisticTypesEmpty -> "Al menos un tipo de estadística es requerido"

        else -> null
    }

)


