package org.eduardoleolim.organizadorpec660.core.municipality.infrastructure

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

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
