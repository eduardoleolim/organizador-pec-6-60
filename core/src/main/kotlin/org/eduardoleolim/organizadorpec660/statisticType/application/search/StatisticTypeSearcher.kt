package org.eduardoleolim.organizadorpec660.statisticType.application.search

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository

class StatisticTypeSearcher(private val repository: StatisticTypeRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
