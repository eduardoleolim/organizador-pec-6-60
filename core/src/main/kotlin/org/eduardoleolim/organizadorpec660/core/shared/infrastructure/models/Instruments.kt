package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Instrument : Entity<Instrument> {
    val id: String
    val statisticYear: Int
    val statisticMonth: Int
    val cosecutive: String
    val saved: Boolean
    val buffer: ByteArray
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
    val instrumentType: InstrumentType
    val statisticType: StatisticType
    val municipality: Municipality
}

class Instruments(alias: String? = null) : Table<Instrument>("instrument", alias) {
    val id = varchar("instrumentId").primaryKey().bindTo { it.id }
    val statisticYear = int("statisticYear").bindTo { it.statisticYear }
    val statisticMonth = int("statisticMonth").bindTo { it.statisticMonth }
    val cosecutive = varchar("cosecutive").bindTo { it.cosecutive }
    val saved = boolean("saved").bindTo { it.saved }
    val buffer = blob("buffer").bindTo { it.buffer }
    val createdAt = datetime("createdAt").bindTo { it.createdAt }
    val updatedAt = datetime("updatedAt").bindTo { it.updatedAt }
    val instrumentTypeId = varchar("instrumentTypeId").references(InstrumentTypes()) { it.instrumentType }
    val statisticTypeId = varchar("statisticTypeId").references(StatisticTypes()) { it.statisticType }
    val municipalityId = varchar("municipalityId").references(Municipalities()) { it.municipality }

    override fun aliased(alias: String) = Instruments(alias)
}
