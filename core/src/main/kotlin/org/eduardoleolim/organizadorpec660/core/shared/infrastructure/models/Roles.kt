package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

interface Role : Entity<Role> {
    val id: String
    val name: String
}

class Roles(alias: String? = null) : Table<Role>("role", alias) {
    val id = varchar("roleId").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }

    override fun aliased(alias: String) = Roles(alias)
}
