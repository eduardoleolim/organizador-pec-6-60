package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface User : Entity<User> {
    val id: String
    val firstname: String
    val lastname: String
    val role: Role
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
}

class Users(alias: String? = null) : Table<User>("user", alias) {
    val id = varchar("userId").primaryKey().bindTo { it.id }
    val firstname = varchar("firstname").bindTo { it.firstname }
    val lastname = varchar("lastname").bindTo { it.lastname }
    val roleId = varchar("roleId").references(Roles()) { it.role }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }

    override fun aliased(alias: String) = Users(alias)
}
