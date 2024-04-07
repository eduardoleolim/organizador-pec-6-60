package org.eduardoleolim.organizadorpec660.core.auth.domain

interface AuthRepository {
    fun search(emailOrUsername: String): Auth?
}
