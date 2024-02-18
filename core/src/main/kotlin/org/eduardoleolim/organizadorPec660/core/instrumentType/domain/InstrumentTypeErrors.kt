package org.eduardoleolim.organizadorPec660.core.instrumentType.domain

import java.util.*

sealed class InstrumentTypeErrors(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidInstrumentTypeIdError(val id: String, override val cause: Throwable?) :
    InstrumentTypeErrors("The id <$id> is not a valid instrument type id", cause)

class InvalidInstrumentTypeNameError(val name: String) :
    InstrumentTypeErrors("The name <$name> is not a valid instrument type name")

class InvalidInstrumentTypeUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InstrumentTypeErrors("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class InstrumentTypeAlreadyExistsError(val name: String) :
    InstrumentTypeErrors("The instrument type with name <$name> already exists")

class InstrumentTypeNotFoundError(val id: String) :
    InstrumentTypeErrors("The instrument type with id <$id> was not found")
