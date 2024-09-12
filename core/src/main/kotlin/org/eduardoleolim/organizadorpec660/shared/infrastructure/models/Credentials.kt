package org.eduardoleolim.organizadorpec660.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface Credential : Entity<Credential> {
    val email: String
    val username: String
    val password: String
    val user: User
}

class Credentials(alias: String? = null) : Table<Credential>("credentials", alias) {
    val email = varchar("email").bindTo { it.email }
    val username = varchar("username").bindTo { it.username }
    val password = varchar("password").bindTo { it.password }
    val userId = varchar("userId").primaryKey().references(Users()) { it.user }

    override fun aliased(alias: String) = Credentials(alias)
}
