package org.eduardoleolim.organizadorPec660.core.statisticType.application.delete

import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeRepository

class StatisticTypeDeleter(private val repository: StatisticTypeRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw StatisticTypeNotFoundError(id)

        repository.delete(id)
    }

    private fun exists(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
