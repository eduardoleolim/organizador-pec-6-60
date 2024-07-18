package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

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
