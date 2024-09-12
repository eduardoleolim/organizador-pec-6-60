package org.eduardoleolim.organizadorpec660.auth.domain

sealed class AuthError(override val message: String, override val cause: Throwable? = null) :
    RuntimeException(message, cause)

class InvalidAuthCredentialsError : AuthError("Credentials are not valid")
