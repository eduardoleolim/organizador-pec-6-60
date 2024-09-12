package org.eduardoleolim.organizadorpec660.auth.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.auth.domain.Auth
import org.eduardoleolim.organizadorpec660.auth.domain.AuthRepository
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Credentials
import org.ktorm.database.Database
import org.ktorm.dsl.*

class KtormAuthRepository(private val database: Database) : AuthRepository {
    private val credentials = Credentials("c")

    override fun search(emailOrUsername: String): Auth? {
        return database
            .from(credentials)
            .select()
            .where { (credentials.email eq emailOrUsername) or (credentials.username eq emailOrUsername) }
            .map { rowSet ->
                credentials.createEntity(rowSet).let {
                    Auth.from(emailOrUsername, it.password)
                }
            }
            .firstOrNull()
    }
}
