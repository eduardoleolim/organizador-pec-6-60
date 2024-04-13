package org.eduardoleolim.organizadorpec660.app.auth.data

sealed class InvalidAuthArgumentException(message: String?) : IllegalArgumentException(message)

class InvalidCredentialsException(val isUsernameEmpty: Boolean, val isPasswordEmpty: Boolean) :
    InvalidAuthArgumentException("Invalid credentials")
