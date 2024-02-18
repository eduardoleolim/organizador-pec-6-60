package org.eduardoleolim.organizadorPec660.app.auth

sealed class InvalidAuthArgumentException : IllegalArgumentException()

class InvalidCredentialsException(val isUsernameInvalid: Boolean, val isPasswordInvalid: Boolean) :
    InvalidAuthArgumentException()
