package org.eduardoleolim.organizadorpec660.role.domain

sealed class RoleError(override val message: String, override val cause: Throwable? = null) : Error(message, cause)


