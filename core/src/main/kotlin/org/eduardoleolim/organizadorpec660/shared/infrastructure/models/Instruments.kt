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
import org.ktorm.schema.*
import java.time.LocalDateTime

interface InstrumentFile : Entity<InstrumentFile> {
    val id: String
    val path: String
}

class InstrumentFiles(alias: String? = null) : Table<InstrumentFile>("instrumentFile", alias) {
    val id = varchar("instrumentFileId").primaryKey().bindTo { it.id }
    val path = varchar("path").bindTo { it.path }

    override fun aliased(alias: String) = InstrumentFiles(alias)
}

interface Instrument : Entity<Instrument> {
    val id: String
    val statisticYear: Int
    val statisticMonth: Int
    val agency: Agency
    val saved: Boolean
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
    val statisticType: StatisticType
    val municipality: Municipality
    val instrumentFile: InstrumentFile
}

class Instruments(alias: String? = null) : Table<Instrument>("instrument", alias) {
    val id = varchar("instrumentId").primaryKey().bindTo { it.id }
    val statisticYear = int("statisticYear").bindTo { it.statisticYear }
    val statisticMonth = int("statisticMonth").bindTo { it.statisticMonth }
    val saved = boolean("saved").bindTo { it.saved }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }
    val agencyId = varchar("agencyId").references(Agencies()) { it.agency }
    val statisticTypeId = varchar("statisticTypeId").references(StatisticTypes()) { it.statisticType }
    val municipalityId = varchar("municipalityId").references(Municipalities()) { it.municipality }
    val instrumentFileId = varchar("instrumentFileId").references(InstrumentFiles()) { it.instrumentFile }

    override fun aliased(alias: String) = Instruments(alias)
}
