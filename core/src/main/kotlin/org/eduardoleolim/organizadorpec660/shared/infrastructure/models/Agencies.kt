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

interface Agency : Entity<Agency> {
    val id: String
    val name: String
    val consecutive: String
    val municipalityId: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
}

class Agencies(alias: String? = null) : Table<Agency>("agency", alias) {
    val id = varchar("agencyId").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val consecutive = varchar("consecutive").bindTo { it.consecutive }
    val municipalityId = varchar("municipalityId").bindTo { it.municipalityId }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }

    override fun aliased(alias: String) = Agencies(alias)
}

interface StatisticTypeOfAgency : Entity<StatisticTypeOfAgency> {
    val agency: Agency
    val statisticType: StatisticType
}

class StatisticTypesOfAgencies(alias: String? = null) : Table<StatisticTypeOfAgency>("statisticType_agency", alias) {
    val agencyId = varchar("agencyId").references(Agencies()) { it.agency }
    val statisticTypeId = varchar("statisticTypeId").references(StatisticTypes()) { it.statisticType }

    override fun aliased(alias: String) = StatisticTypesOfAgencies(alias)
}
