package org.eduardoleolim.organizadorPec660.core.federalEntity.application.search

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

class FederalEntitySearcher(private val repository: FederalEntityRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
