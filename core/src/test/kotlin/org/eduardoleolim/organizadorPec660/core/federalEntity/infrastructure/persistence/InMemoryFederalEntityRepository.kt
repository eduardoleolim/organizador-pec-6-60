package org.eduardoleolim.organizadorPec660.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

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
