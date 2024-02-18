package org.eduardoleolim.organizadorPec660.core.auth.domain

interface AuthRepository {
    fun search(emailOrUsername: String): Auth?
}
