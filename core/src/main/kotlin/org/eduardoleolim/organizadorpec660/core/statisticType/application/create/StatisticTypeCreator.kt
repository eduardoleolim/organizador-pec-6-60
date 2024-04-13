package org.eduardoleolim.organizadorpec660.core.statisticType.application.create

import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class StatisticTypeCreator(private val statisticTypeRepository: StatisticTypeRepository) {
    fun create(keyCode: String, name: String) {
        if (existsStatisticType(keyCode))
            throw StatisticTypeAlreadyExistsError(keyCode)

        StatisticType.create(keyCode, name).let {
            statisticTypeRepository.save(it)
        }
    }

    private fun existsStatisticType(keyCode: String) = StatisticTypeCriteria.keyCodeCriteria(keyCode).let {
        statisticTypeRepository.count(it) > 0
    }
}
