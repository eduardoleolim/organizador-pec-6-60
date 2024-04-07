package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface InstrumentType : Entity<InstrumentType> {
    val id: String
    val name: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
}

class InstrumentTypes(alias: String? = null) : Table<InstrumentType>("instrumentType", alias) {
    val id = varchar("instrumentTypeId").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }

    override fun aliased(alias: String) = InstrumentTypes(alias)
}
