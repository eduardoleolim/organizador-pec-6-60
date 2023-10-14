package org.eduardoleolim.core.federalEntity.infrastructure.persistence

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import java.util.*

class InMemoryFederalEntityRepository : FederalEntityRepository {
    val records: MutableMap<UUID, FederalEntity> = mutableMapOf()

    override fun matching(criteria: Criteria): List<FederalEntity> {
        InMemoryFederalEntitiesCriteriaParser.apply {
            return records.values.toList().let {
                applyFilters(it, criteria)
            }.let {
                applyOrders(it, criteria)
            }.let {
                applyPagination(it, criteria.limit, criteria.offset)
            }
        }
    }

    override fun count(criteria: Criteria): Int {
        InMemoryFederalEntitiesCriteriaParser.apply {
            return records.values.toList().let {
                applyFilters(it, criteria)
            }.let {
                applyOrders(it, criteria)
            }.let {
                applyPagination(it, criteria.limit, criteria.offset)
            }.size
        }
    }

    override fun save(federalEntity: FederalEntity) {
        records[federalEntity.id()] = federalEntity
    }

    override fun delete(federalEntityId: FederalEntityId) {
        records.remove(federalEntityId.value)
    }
}
