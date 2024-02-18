package org.eduardoleolim.organizadorPec660.core.statisticType.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface StatisticTypeRepository {
    fun matching(criteria: Criteria): List<StatisticType>

    fun count(criteria: Criteria): Int

    fun save(statisticType: StatisticType)

    fun delete(statisticTypeId: String)
}
