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

package org.eduardoleolim.organizadorpec660.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

class InMemoryFederalEntityRepository : FederalEntityRepository {
    val records: MutableMap<String, FederalEntity> = mutableMapOf()

    override fun matching(criteria: Criteria): List<FederalEntity> {
        return InMemoryFederalEntitiesCriteriaParser.run {
            records.values.toMutableList()
                .run { applyFilters(this, criteria) }
                .run { applyOrders(this, criteria) }
                .run { applyPagination(this, criteria.limit, criteria.offset) }
        }
    }

    override fun count(criteria: Criteria): Int {
        return InMemoryFederalEntitiesCriteriaParser.run {
            records.values.toList()
                .run { applyFilters(this, criteria) }
                .run { applyOrders(this, criteria) }
                .run { applyPagination(this, criteria.limit, criteria.offset) }.size
        }
    }

    override fun save(federalEntity: FederalEntity) {
        records[federalEntity.id().toString()] = federalEntity
    }

    override fun delete(federalEntityId: String) {
        records.remove(federalEntityId)
    }
}
