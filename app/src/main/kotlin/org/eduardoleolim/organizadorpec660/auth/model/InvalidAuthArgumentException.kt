package org.eduardoleolim.organizadorpec660.auth.model

sealed class InvalidAuthArgumentException(message: String?) : IllegalArgumentException(message)

class InvalidCredentialsException(val isUsernameEmpty: Boolean, val isPasswordEmpty: Boolean) :
    InvalidAuthArgumentException("Invalid credentials")
