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

package org.eduardoleolim.organizadorpec660.municipality.infrastructure

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

class InMemoryMunicipalityRepository : MunicipalityRepository {
    val municipalities: MutableMap<String, Municipality> = mutableMapOf()
    val federalEntities: MutableMap<String, FederalEntity> = mutableMapOf()

    override fun matching(criteria: Criteria): List<Municipality> {
        return InMemoryMunicipalityCriteriaParser.run {
            municipalities.values.toMutableList()
                .run { applyFilters(this, federalEntities.values.toList(), criteria) }
                .run { applyOrders(this, federalEntities.values.toList(), criteria) }
                .run { applyPagination(this, criteria.limit, criteria.offset) }
        }
    }

    override fun count(criteria: Criteria): Int {
        return InMemoryMunicipalityCriteriaParser.run {
            municipalities.values.toMutableList()
                .run { applyFilters(this, federalEntities.values.toList(), criteria) }
                .run { applyOrders(this, federalEntities.values.toList(), criteria) }
                .run { applyPagination(this, criteria.limit, criteria.offset) }.size
        }
    }

    override fun save(municipality: Municipality) {
        municipalities[municipality.id().toString()] = municipality
    }

    override fun delete(municipalityId: String) {
        municipalities.remove(municipalityId)
    }
}
