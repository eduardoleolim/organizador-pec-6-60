package org.eduardoleolim.organizadorpec660.federalEntity.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface FederalEntityRepository {
    fun matching(criteria: Criteria): List<FederalEntity>

    fun count(criteria: Criteria): Int

    fun save(federalEntity: FederalEntity)

    fun delete(federalEntityId: String)
}
