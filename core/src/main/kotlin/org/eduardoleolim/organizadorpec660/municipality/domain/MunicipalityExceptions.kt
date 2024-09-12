package org.eduardoleolim.organizadorpec660.municipality.domain

import java.util.*

sealed class InvalidArgumentMunicipalityException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidMunicipalityIdError(val id: String, override val cause: Throwable?) :
    InvalidArgumentMunicipalityException("The id <$id> is not a valid municipality id", cause)

class InvalidMunicipalityKeyCodeError(val keyCode: String) :
    InvalidArgumentMunicipalityException("The key code <$keyCode> is not a valid municipality key code")

class InvalidMunicipalityUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidArgumentMunicipalityException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")

class InvalidMunicipalityNameError(val name: String) :
    InvalidArgumentMunicipalityException("The name <$name> is not a valid municipality name")
