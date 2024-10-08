/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
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

package org.eduardoleolim.organizadorpec660.agency.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Agencies
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Municipalities
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.StatisticTypesOfAgencies
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormAgencyRepository(private val database: Database) : AgencyRepository {
    private val agencies = Agencies("ac")
    private val municipalities = Municipalities("m")
    private val statisticTypesOfAgencies = StatisticTypesOfAgencies("s_ac")
    private val criteriaParser =
        KtormAgenciesCriteriaParser(database, agencies, municipalities, statisticTypesOfAgencies)

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
                    set(it.statisticTypeId, statisticTypeId.value.toString())

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
