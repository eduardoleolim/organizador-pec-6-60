package org.eduardoleolim.organizadorpec660.core.statisticType.application.search

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class StatisticTypeSearcher(private val repository: StatisticTypeRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
