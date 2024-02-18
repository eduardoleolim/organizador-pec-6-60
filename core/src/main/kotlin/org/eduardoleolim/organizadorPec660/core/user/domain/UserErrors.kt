package org.eduardoleolim.organizadorPec660.core.user.domain

import java.util.*

sealed class UserError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidUserIdError(val id: String, override val cause: Throwable?) :
    UserError("The id <$id> is not a valid user id", cause)

class InvalidUserUpdateDateError(val updatedAt: Date, val createdAt: Date) :
    UserError("The updatedAt <$updatedAt> is not a valid user update date", null)

class UserNotFoundError(val id: String) :
    UserError("The user with id <$id> was not found", null)
