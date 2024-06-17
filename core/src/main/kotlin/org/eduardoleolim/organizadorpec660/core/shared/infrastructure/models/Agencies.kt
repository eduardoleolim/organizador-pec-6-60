package org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models

import org.ktorm.entity.Entity
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Agency : Entity<Agency> {
    val id: String
    val name: String
    val consecutive: Int
    val municipalityId: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime?
}

class Agencies(alias: String? = null) : Table<Agency>("agency", alias) {
    val id = varchar("agencyId").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val consecutive = int("consecutive").bindTo { it.consecutive }
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
