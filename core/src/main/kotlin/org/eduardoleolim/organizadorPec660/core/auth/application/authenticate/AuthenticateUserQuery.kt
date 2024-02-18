package org.eduardoleolim.organizadorPec660.core.auth.application.authenticate

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query

class AuthenticateUserQuery(private val emailOrUsername: String, private val password: String) : Query {
    fun emailOrUsername() = emailOrUsername

    fun password() = password
}
