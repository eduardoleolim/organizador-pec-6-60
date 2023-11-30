package org.eduardoleolim.organizadorpec660.app.auth

sealed class InvalidAuthArgumentException : IllegalArgumentException()

class InvalidCredentialsException(val isUsernameInvalid: Boolean, val isPasswordInvalid: Boolean) :
    InvalidAuthArgumentException()
