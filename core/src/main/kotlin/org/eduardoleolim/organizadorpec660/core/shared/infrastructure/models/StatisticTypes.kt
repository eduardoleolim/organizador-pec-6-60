package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar
import java.time.LocalDateTime

interface StatisticType : Entity<StatisticType> {
    val id: String
    val keyCode: String
    val name: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
}

class StatisticTypes(alias: String? = null) : Table<StatisticType>("statisticType", alias) {
    val id = varchar("statisticTypeId").primaryKey().bindTo { it.id }
    val keyCode = varchar("keyCode").bindTo { it.keyCode }
    val name = varchar("name").bindTo { it.name }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }

    override fun aliased(alias: String) = StatisticTypes(alias)
}
