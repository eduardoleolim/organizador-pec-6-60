package org.eduardoleolim.organizadorpec660.federalEntity.application.search

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

class FederalEntitySearcher(private val repository: FederalEntityRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
