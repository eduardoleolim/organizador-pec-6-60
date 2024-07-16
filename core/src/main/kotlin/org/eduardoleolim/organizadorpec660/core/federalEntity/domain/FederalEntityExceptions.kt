package org.eduardoleolim.organizadorpec660.core.federalEntity.domain

import java.util.*

sealed class InvalidFederalEntityException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidFederalEntityIdError(val id: String, override val cause: Throwable?) :
    InvalidFederalEntityException("The id <$id> is not a valid federal entity id", cause)

class InvalidFederalEntityKeyCodeError(val keyCode: String) :
    InvalidFederalEntityException("The key code <$keyCode> is not a valid federal entity key code")

class InvalidFederalEntityNameError(val name: String) :
    InvalidFederalEntityException("The name <$name> is not a valid federal entity name")

class InvalidFederalEntityUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    InvalidFederalEntityException("The update date <$updatedAt> is not valid because it is before the create date <$createdAt>")
