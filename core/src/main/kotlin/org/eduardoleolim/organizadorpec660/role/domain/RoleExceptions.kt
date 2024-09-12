package org.eduardoleolim.organizadorpec660.role.domain

sealed class InvalidRoleException(override val message: String, override val cause: Throwable? = null) :
    IllegalArgumentException(message, cause)

class InvalidRoleIdException(val id: String, override val cause: Throwable?) :
    InvalidRoleException("The id <$id> is not a valid role id", cause)
