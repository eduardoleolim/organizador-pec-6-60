package org.eduardoleolim.organizadorpec660.auth.application.authenticate

import org.eduardoleolim.organizadorpec660.auth.domain.AuthRepository

class UserAuthenticator(private val authRepository: AuthRepository) {
    fun authenticate(emailOrUsername: String, password: String): Boolean {
        authRepository.search(emailOrUsername)?.let {
            return it.password() == password
        } ?: return false
    }
}
