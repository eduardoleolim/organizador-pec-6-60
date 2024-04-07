package org.eduardoleolim.organizadorpec660.app.instrumentType.data

sealed class InvalidInstrumentTypeArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyInstrumentTypeDataException : InvalidInstrumentTypeArgumentException("El nombre es requerido")
