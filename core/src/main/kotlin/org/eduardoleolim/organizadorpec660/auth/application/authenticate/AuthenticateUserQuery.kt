package org.eduardoleolim.organizadorpec660.auth.application.authenticate

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class AuthenticateUserQuery(private val emailOrUsername: String, private val password: String) : Query {
    fun emailOrUsername() = emailOrUsername

    fun password() = password
}
