/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.shared.infrastructure.models

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
