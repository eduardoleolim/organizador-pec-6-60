package org.eduardoleolim.core.municipality.domain

import java.util.*

sealed class MunicipalityErrors(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidMunicipalityIdError(val id: String, override val cause: Throwable?) :
    MunicipalityErrors("The id <$id> is not a valid municipality id", cause)

class InvalidMunicipalityKeyCodeError(val keyCode: String) :
    MunicipalityErrors("The key code <$keyCode> is not a valid municipality key code")

class InvalidMunicipalityUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    MunicipalityErrors("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class InvalidMunicipalityNameError(val name: String) :
    MunicipalityErrors("The name <$name> is not a valid municipality name")

class MunicipalityAlreadyExistsError(val keyCode: String) :
    MunicipalityErrors("The municipality with key code <$keyCode> already exists")

