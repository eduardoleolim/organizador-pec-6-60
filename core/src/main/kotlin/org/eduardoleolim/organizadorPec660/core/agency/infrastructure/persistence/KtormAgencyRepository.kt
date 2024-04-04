package org.eduardoleolim.organizadorPec660.core.agency.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.agency.domain.Agency
import org.eduardoleolim.organizadorPec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorPec660.core.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorPec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorPec660.core.shared.domain.toDate
import org.eduardoleolim.organizadorPec660.core.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.Agencies
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.AgenciesOfMunicipalities
import org.eduardoleolim.organizadorPec660.core.shared.infrastructure.models.StatisticTypesOfAgencies
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormAgencyRepository(private val database: Database) : AgencyRepository {
    private val agencies = Agencies("ac")
    private val agenciesOfMunicipalities = AgenciesOfMunicipalities("ac_m")
    private val statisticTypesOfAgencies = StatisticTypesOfAgencies("s_ac")
    private val criteriaParser =
        KtormAgenciesCriteriaParser(database, agencies, agenciesOfMunicipalities, statisticTypesOfAgencies)

    override fun matching(criteria: Criteria): List<Agency> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            val agency = agencies.createEntity(rowSet)
            val municipalityAssociations = searchMunicipalityAssociations(agency.id)
            val statisticTypeAssociations = searchStatisticTypeAssociations(agency.id)

            Agency.from(
                agency.id,
                agency.name,
                agency.consecutive,
                municipalityAssociations,
                statisticTypeAssociations,
                agency.createdAt.toDate(),
                agency.updatedAt?.toDate()
            )
        }
    }

    private fun searchMunicipalityAssociations(agencyId: String): List<Pair<String, Boolean>> {
        return database.from(agenciesOfMunicipalities)
            .select()
            .where { agenciesOfMunicipalities.agencyId eq agencyId }
            .map {
                Pair(it[agenciesOfMunicipalities.municipalityId]!!, it[agenciesOfMunicipalities.isOwner]!!)
            }
    }

    private fun searchStatisticTypeAssociations(agencyId: String): List<Pair<String, String>> {
        return database.from(statisticTypesOfAgencies)
            .select()
            .where { statisticTypesOfAgencies.agencyId eq agencyId }
            .map {
                Pair(it[statisticTypesOfAgencies.statisticTypeId]!!, it[statisticTypesOfAgencies.instrumentTypeId]!!)
            }
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
                set(it.createdAt, agency.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.name, agency.name())
                    set(it.consecutive, agency.consecutive())
                    set(it.updatedAt, agency.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }

            database.delete(agenciesOfMunicipalities) {
                val municipalityIds = agency.municipalities().map { municipality ->
                    municipality.municipalityId().toString()
                }

                (it.municipalityId notInList municipalityIds) and (it.agencyId eq agency.id().toString())
            }

            agency.municipalities().map { municipality ->
                database.insertOrUpdate(agenciesOfMunicipalities) {
                    set(it.agencyId, municipality.agencyId().toString())
                    set(it.municipalityId, municipality.municipalityId().toString())
                    set(it.isOwner, municipality.isOwner())

                    onConflict(it.agencyId, it.municipalityId) {
                        set(it.isOwner, municipality.isOwner())
                    }
                }
            }

            database.delete(statisticTypesOfAgencies) {
                val statisticTypeIds = agency.statisticTypes().map { statisticType ->
                    statisticType.statisticTypeId().toString()
                }

                (it.statisticTypeId notInList statisticTypeIds) and (it.agencyId eq agency.id().toString())
            }

            agency.statisticTypes().map { statisticType ->
                database.insertOrUpdate(statisticTypesOfAgencies) {
                    set(it.agencyId, statisticType.agencyId().toString())
                    set(it.statisticTypeId, statisticType.statisticTypeId().toString())
                    set(it.instrumentTypeId, statisticType.instrumentTypeId().toString())

                    onConflict(it.agencyId, it.statisticTypeId) {
                        set(it.instrumentTypeId, statisticType.instrumentTypeId().toString())
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

            database.delete(agenciesOfMunicipalities) {
                it.agencyId eq agencyId
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
