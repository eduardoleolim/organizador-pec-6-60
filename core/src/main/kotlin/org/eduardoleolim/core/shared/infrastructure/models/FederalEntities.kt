package org.eduardoleolim.core.shared.infrastructure.models

import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object FederalEntities : Table<Nothing>("federalEntity") {
    val id = uuid("federalEntityId").primaryKey()
    val keyCode = varchar("keyCode")
    val name = varchar("name")
    val createdAt = date("createdAt")
    val updatedAt = date("updatedAt")
}
