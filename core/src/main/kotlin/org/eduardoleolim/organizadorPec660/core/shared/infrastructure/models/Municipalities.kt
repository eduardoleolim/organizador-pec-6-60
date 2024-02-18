package org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface Municipality : Entity<Municipality> {
    val id: String
    val keyCode: String
    val name: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
    val federalEntity: FederalEntity
}

class Municipalities(alias: String? = null) : Table<Municipality>("municipality", alias) {
    val id = varchar("municipalityId").primaryKey().bindTo { it.id }
    val keyCode = varchar("keyCode").bindTo { it.keyCode }
    val name = varchar("name").bindTo { it.name }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }
    val federalEntityId = varchar("federalEntityId").references(FederalEntities()) { it.federalEntity }

    override fun aliased(alias: String) = Municipalities(alias)
}
