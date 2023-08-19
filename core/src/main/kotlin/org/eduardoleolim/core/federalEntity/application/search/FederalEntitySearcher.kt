package org.eduardoleolim.core.federalEntity.application.search

import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.shared.domain.criteria.Criteria

class FederalEntitySearcher(private val repository: FederalEntityRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
