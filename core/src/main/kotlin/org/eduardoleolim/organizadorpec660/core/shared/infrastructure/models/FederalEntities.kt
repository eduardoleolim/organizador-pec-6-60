package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface FederalEntity : Entity<FederalEntity> {
    val id: String
    val keyCode: String
    val name: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
}

class FederalEntities(alias: String? = null) : Table<FederalEntity>("federalEntity", alias) {
    val id = varchar("federalEntityId").primaryKey().bindTo { it.id }
    val keyCode = varchar("keyCode").bindTo { it.keyCode }
    val name = varchar("name").bindTo { it.name }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }

    override fun aliased(alias: String) = FederalEntities(alias)
}
