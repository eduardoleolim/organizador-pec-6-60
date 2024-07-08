package org.eduardoleolim.organizadorpec660.app.federalEntity.data

sealed class InvalidFederalEntityArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyFederalEntityDataException(val isKeyCodeEmpty: Boolean, val isNameEmpty: Boolean) :
    InvalidFederalEntityArgumentException(
        when {
            isKeyCodeEmpty && isNameEmpty -> "La clave y el nombre son requeridos"
            isKeyCodeEmpty -> "La clave es requerida"
            isNameEmpty -> "El nombre es requerido"
            else -> null
        }
    )

class FederalEntityImportException(val warnings: List<Throwable>) :
    InvalidFederalEntityArgumentException("An error occurred during export of federal entities")
