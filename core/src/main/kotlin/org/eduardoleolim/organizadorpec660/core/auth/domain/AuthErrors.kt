package org.eduardoleolim.organizadorpec660.core.auth.domain

sealed class AuthError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidAuthCredentialsError : AuthError("Credentials are not valid")

class AuthNotFoundError : AuthError("User not found")
