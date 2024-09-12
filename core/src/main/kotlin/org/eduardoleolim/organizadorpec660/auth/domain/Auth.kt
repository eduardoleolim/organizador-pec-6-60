package org.eduardoleolim.organizadorpec660.auth.domain

class Auth private constructor(
    private val emailOrUsername: AuthEmailOrUsername,
    private val password: AuthPassword
) {
    companion object {
        fun from(emailOrUsername: String, password: String) = Auth(
            AuthEmailOrUsername(emailOrUsername),
            AuthPassword(password)
        )
    }

    fun emailOrUsername() = emailOrUsername

    fun password() = password.value
}

data class AuthEmailOrUsername(val value: String)

data class AuthPassword(val value: String)
