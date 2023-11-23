package org.eduardoleolim.organizadorpec660.core.user.domain

class Credentials private constructor(
    private var email: CredentialsEmail,
    private var username: CredentialsUsername,
    private var password: CredentialsPassword
) {
    companion object {
        fun from(email: String, username: String, password: String) = Credentials(
            CredentialsEmail(email),
            CredentialsUsername(username),
            CredentialsPassword(password)
        )
    }

    fun email() = email.value

    fun username() = username.value

    fun password() = password.value

    fun changeEmail(email: String) {
        this.email = CredentialsEmail(email)
    }

    fun changeUsername(username: String) {
        this.username = CredentialsUsername(username)
    }

    fun changePassword(password: String) {
        this.password = CredentialsPassword(password)
    }
}

data class CredentialsEmail(val value: String)

data class CredentialsUsername(val value: String)

data class CredentialsPassword(val value: String)
