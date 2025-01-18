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

package org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.shared.domain.toDate
import org.eduardoleolim.organizadorpec660.shared.domain.toLocalDateTime
import org.eduardoleolim.organizadorpec660.shared.infrastructure.models.FederalEntities
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.map
import org.ktorm.support.sqlite.insertOrUpdate
import java.time.LocalDateTime

class KtormFederalEntityRepository(private val database: Database) : FederalEntityRepository {
    private val federalEntities = FederalEntities("f")
    private val criteriaParser = KtormFederalEntitiesCriteriaParser(database, federalEntities)

    override fun matching(criteria: Criteria): List<FederalEntity> {
        val query = criteriaParser.selectQuery(criteria)

        return query.map { rowSet ->
            federalEntities.createEntity(rowSet).let {
                FederalEntity.from(it.id, it.keyCode, it.name, it.createdAt.toDate(), it.updatedAt?.toDate())
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

    override fun save(federalEntity: FederalEntity) {
        database.useTransaction {
            database.insertOrUpdate(federalEntities) {
                set(it.id, federalEntity.id().toString())
                set(it.keyCode, federalEntity.keyCode())
                set(it.name, federalEntity.name())
                set(it.createdAt, federalEntity.createdAt().toLocalDateTime())

                onConflict(it.id) {
                    set(it.keyCode, federalEntity.keyCode())
                    set(it.name, federalEntity.name())
                    set(it.updatedAt, federalEntity.updatedAt()?.toLocalDateTime() ?: LocalDateTime.now())
                }
            }
        }
    }

    override fun delete(federalEntityId: String) {
        database.useTransaction {
            val count = count(FederalEntityCriteria.idCriteria(federalEntityId))

            if (count == 0)
                throw FederalEntityNotFoundError(federalEntityId)

            database.delete(federalEntities) {
                it.id eq federalEntityId
            }
        }
    }
}
