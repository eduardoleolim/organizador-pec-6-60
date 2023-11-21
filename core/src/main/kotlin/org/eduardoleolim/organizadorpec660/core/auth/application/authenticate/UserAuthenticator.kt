package org.eduardoleolim.organizadorpec660.core.auth.application.authenticate

import org.eduardoleolim.organizadorpec660.core.auth.domain.AuthNotFoundError
import org.eduardoleolim.organizadorpec660.core.auth.domain.AuthRepository
import org.eduardoleolim.organizadorpec660.core.auth.domain.InvalidAuthCredentialsError

class UserAuthenticator(private val authRepository: AuthRepository) {
    fun authenticate(emailOrUsername: String, password: String) {
        authRepository.search(emailOrUsername)?.let {
            if (it.password() != password)
                throw InvalidAuthCredentialsError()
        } ?: throw AuthNotFoundError()
    }
}
