package org.eduardoleolim.organizadorPec660.app.statisticType

sealed class InvalidStatisticTypeArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyStatisticTypeDataException(
    val isKeyCodeEmpty: Boolean,
    val isNameEmpty: Boolean,
    val isInstrumentTypesEmpty: Boolean
) :
    InvalidStatisticTypeArgumentException(
        when {
            isKeyCodeEmpty && isNameEmpty && isInstrumentTypesEmpty -> "La clave, el nombre y al menos un tipo de instrumento son requeridos"
            isKeyCodeEmpty && isNameEmpty -> "La clave y el nombre son requeridos"
            isNameEmpty && isInstrumentTypesEmpty -> "El nombre y al menos un tipo de instrumento son requeridos"
            isKeyCodeEmpty -> "La clave es requerida"
            isNameEmpty -> "El nombre es requerido"
            isInstrumentTypesEmpty -> "Al menos un tipo de instrumento es requerido"
            else -> null
        }
    )
