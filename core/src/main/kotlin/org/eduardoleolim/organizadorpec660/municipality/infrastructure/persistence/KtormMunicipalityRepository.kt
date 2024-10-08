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

package org.eduardoleolim.organizadorpec660.municipality.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.FederalEntities
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.Municipalities
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormMunicipalityRepository(private val database: Database) : MunicipalityRepository {
    private val municipalities = Municipalities("m")
    private val federalEntities = FederalEntities("f")
    private val criteriaParser = KtormMunicipalitiesCriteriaParser(database, municipalities, federalEntities)

    override fun matching(criteria: Criteria): List<Municipality> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            municipalities.createEntity(rowSet).let {
                Municipality.from(
                    it.id,
                    it.keyCode,
                    it.name,
                    it.federalEntity.id,
                    it.createdAt.toDate(),
                    it.updatedAt?.toDate()
                )
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        val query = criteriaParser.countQuery(criteria)

        return query.rowSet.let {
            it.next()
            it.getInt(1)
        }
    }

    override fun save(municipality: Municipality) {
        database.useTransaction {
            database.insertOrUpdate(municipalities) {
                set(it.id, municipality.id().toString())
                set(it.keyCode, municipality.keyCode())
                set(it.name, municipality.name())
                set(it.federalEntityId, municipality.federalEntityId().toString())
                set(it.createdAt, municipality.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.keyCode, municipality.keyCode())
                    set(it.name, municipality.name())
                    set(it.federalEntityId, municipality.federalEntityId().toString())
                    set(it.updatedAt, municipality.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }
        }
    }

    override fun delete(municipalityId: String) {
        database.useTransaction {
            val count = count(MunicipalityCriteria.idCriteria(municipalityId))

            if (count == 0)
                throw MunicipalityNotFoundError(municipalityId)

            database.delete(municipalities) {
                it.id eq municipalityId
            }
        }
    }
}
