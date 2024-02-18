package org.eduardoleolim.organizadorPec660.core.auth.application.authenticate

import org.eduardoleolim.organizadorPec660.core.auth.domain.AuthRepository

class UserAuthenticator(private val authRepository: AuthRepository) {
    fun authenticate(emailOrUsername: String, password: String): Boolean {
        authRepository.search(emailOrUsername)?.let {
            return it.password() == password
        } ?: return false
    }
}
