package org.eduardoleolim.organizadorpec660.auth.domain

interface AuthRepository {
    fun search(emailOrUsername: String): Auth?
}
