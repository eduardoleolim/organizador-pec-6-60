package org.eduardoleolim.organizadorPec660.core.auth.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.auth.domain.Auth
import org.eduardoleolim.organizadorPec660.core.auth.domain.AuthRepository
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.Credentials
import org.ktorm.database.Database
import org.ktorm.dsl.*

class KtormAuthRepository(private val database: Database) : AuthRepository {
    private val credentials = Credentials("c")

    override fun search(emailOrUsername: String): Auth? {
        return database
            .from(credentials)
            .select()
            .where { (credentials.email eq emailOrUsername) or (credentials.username eq emailOrUsername) }
            .map { Auth.from(emailOrUsername, it[credentials.password]!!) }
            .firstOrNull()
    }
}
