package org.eduardoleolim.organizadorpec660.core.agency.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.Agencies
import org.eduardoleolim.organizadorpec660.core.shared.infrastructure.models.StatisticTypesOfAgencies
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.sqlite.bulkInsert
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormAgencyRepository(private val database: Database) : AgencyRepository {
    private val agencies = Agencies("ac")
    private val statisticTypesOfAgencies = StatisticTypesOfAgencies("s_ac")
    private val criteriaParser = KtormAgenciesCriteriaParser(database, agencies, statisticTypesOfAgencies)

    override fun matching(criteria: Criteria): List<Agency> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            val agency = agencies.createEntity(rowSet)
            val statisticTypeAssociations = searchStatisticTypes(agency.id)

            Agency.from(
                agency.id,
                agency.name,
                agency.consecutive,
                agency.municipalityId,
                statisticTypeAssociations,
                agency.createdAt.toDate(),
                agency.updatedAt?.toDate()
            )
        }
    }

    private fun searchStatisticTypes(agencyId: String): List<String> {
        return database.from(statisticTypesOfAgencies)
            .select()
            .where { statisticTypesOfAgencies.agencyId eq agencyId }
            .map { it[statisticTypesOfAgencies.statisticTypeId]!! }
    }

    override fun count(criteria: Criteria): Int {
        val query = criteriaParser.countQuery(criteria)

        return query.rowSet.let {
            it.next()
            it.getInt(1)
        }
    }

    override fun save(agency: Agency) {
        database.useTransaction {
            database.insertOrUpdate(agencies) {
                set(it.id, agency.id().toString())
                set(it.name, agency.name())
                set(it.consecutive, agency.consecutive())
                set(it.municipalityId, agency.municipalityId().toString())
                set(it.createdAt, agency.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.name, agency.name())
                    set(it.consecutive, agency.consecutive())
                    set(it.municipalityId, agency.municipalityId().toString())
                    set(it.updatedAt, agency.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }

            database.delete(statisticTypesOfAgencies) {
                val statisticTypeIds = agency.statisticTypeIds().map { statisticTypeId ->
                    statisticTypeId.value.toString()
                }

                (it.statisticTypeId notInList statisticTypeIds) and (it.agencyId eq agency.id().toString())
            }

            agency.statisticTypeIds().forEach { statisticTypeId ->
                database.insertOrUpdate(statisticTypesOfAgencies) {
                    set(it.agencyId, agency.id().toString())
                    set(it.statisticTypeId, statisticTypeId.toString())

                    onConflict(it.agencyId, it.statisticTypeId) {
                        doNothing()
                    }
                }
            }
        }
    }

    override fun delete(agencyId: String) {
        database.useTransaction {
            val count = count(AgencyCriteria.idCriteria(agencyId))

            if (count == 0) {
                throw AgencyNotFoundError(agencyId)
            }

            database.delete(statisticTypesOfAgencies) {
                it.agencyId eq agencyId
            }

            database.delete(agencies) {
                it.id eq agencyId
            }
        }
    }
}
