package org.eduardoleolim.organizadorPec660.app.instrumentType

sealed class InvalidInstrumentTypeArgumentException(message: String?) : IllegalArgumentException(message)

class EmptyInstrumentTypeDataException : InvalidInstrumentTypeArgumentException("El nombre es requerido")
