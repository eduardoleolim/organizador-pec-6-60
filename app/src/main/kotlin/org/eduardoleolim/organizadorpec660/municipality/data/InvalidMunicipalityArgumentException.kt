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

package org.eduardoleolim.organizadorpec660.municipality.data

sealed class InvalidMunicipalityArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyMunicipalityDataException(
    val isFederalEntityEmpty: Boolean,
    val isKeyCodeEmpty: Boolean,
    val isNameEmpty: Boolean
) :
    InvalidMunicipalityArgumentException(
        when {
            isFederalEntityEmpty && isKeyCodeEmpty && isNameEmpty -> "The federal entity, keyCode and name are required"
            isFederalEntityEmpty && isKeyCodeEmpty -> "The federal entity and keyCode are required"
            isFederalEntityEmpty && isNameEmpty -> "The federal entity and name are required"
            isKeyCodeEmpty && isNameEmpty -> "The keyCode and name are required"
            isFederalEntityEmpty -> "The federal entity is required"
            isKeyCodeEmpty -> "The keyCode is required"
            isNameEmpty -> "The name is required"
            else -> "No data is missing"
        }
    )
