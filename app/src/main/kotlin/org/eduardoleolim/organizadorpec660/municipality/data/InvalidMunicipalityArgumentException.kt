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
