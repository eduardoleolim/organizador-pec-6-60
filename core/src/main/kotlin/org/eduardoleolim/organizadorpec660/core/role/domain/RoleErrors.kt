package org.eduardoleolim.organizadorpec660.core.role.domain

sealed class RoleError(override val message: String, override val cause: Throwable? = null) : Error(message, cause)


