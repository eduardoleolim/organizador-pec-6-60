package org.eduardoleolim.organizadorPec660.app.auth.data

sealed class InvalidAuthArgumentException(message: String?) : IllegalArgumentException(message)

class InvalidCredentialsException(val isUsernameInvalid: Boolean, val isPasswordInvalid: Boolean) :
    InvalidAuthArgumentException("Credenciales inválidas")
