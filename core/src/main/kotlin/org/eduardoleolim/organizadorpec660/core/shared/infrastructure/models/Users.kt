package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface User : Entity<User> {
    val id: String
    val firstname: String
    val lastname: String
    val role: Role
}

class Users(alias: String? = null) : Table<User>("user", alias) {
    val id = varchar("userId").primaryKey().bindTo { it.id }
    val firstname = varchar("firstname").bindTo { it.firstname }
    val lastname = varchar("lastname").bindTo { it.lastname }
    val roleId = varchar("roleId").references(Roles()) { it.role }

    override fun aliased(alias: String) = Users(alias)
}
