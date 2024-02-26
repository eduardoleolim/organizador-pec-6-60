package org.eduardoleolim.organizadorPec660.app.municipality

sealed class InvalidMunicipalityArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyMunicipalityDataException(
    val isFederalEntityEmpty: Boolean,
    val isKeyCodeEmpty: Boolean,
    val isNameEmpty: Boolean
) :
    InvalidMunicipalityArgumentException(
        when {
            isFederalEntityEmpty && isKeyCodeEmpty && isNameEmpty -> "La entidad federativa, la clave y el nombre son requeridos"
            isFederalEntityEmpty && isKeyCodeEmpty -> "La entidad federativa y la clave son requeridas"
            isFederalEntityEmpty && isNameEmpty -> "La entidad federativa y el nombre son requeridos"
            isKeyCodeEmpty && isNameEmpty -> "La clave y el nombre son requeridos"
            isFederalEntityEmpty -> "La entidad federativa es requiridad"
            isKeyCodeEmpty -> "La clave es requerida"
            isNameEmpty -> "El nombre es requerido"
            else -> null
        }
    )
