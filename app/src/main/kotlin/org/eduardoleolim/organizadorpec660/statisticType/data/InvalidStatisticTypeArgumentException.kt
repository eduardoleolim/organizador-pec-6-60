package org.eduardoleolim.organizadorpec660.statisticType.data

sealed class InvalidStatisticTypeArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyStatisticTypeDataException(
    val isKeyCodeEmpty: Boolean,
    val isNameEmpty: Boolean
) :
    InvalidStatisticTypeArgumentException(
        when {
            isKeyCodeEmpty && isNameEmpty -> "La clave y el nombre son requeridos"
            isKeyCodeEmpty -> "La clave es requerida"
            isNameEmpty -> "El nombre es requerido"
            else -> null
        }
    )
