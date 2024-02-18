package org.eduardoleolim.organizadorPec660.core.role.domain

sealed class RoleError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidRoleIdError(val id: String, override val cause: Throwable?) :
    RoleError("The id <$id> is not a valid role id", cause)
