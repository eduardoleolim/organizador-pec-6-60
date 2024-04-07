package org.eduardoleolim.organizadorpec660.core.statisticType.application.update

import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class StatisticTypeUpdater(private val statisticTypeRepository: StatisticTypeRepository) {
    fun update(id: String, keyCode: String, name: String) {
        val statisticType = searchStatisticType(id) ?: throw StatisticTypeNotFoundError(id)

        if (existsAnotherSameKeyCode(id, keyCode))
            throw StatisticTypeAlreadyExistsError(keyCode)

        statisticType.apply {
            changeKeyCode(keyCode)
            changeName(name)
        }.let {
            statisticTypeRepository.save(it)
        }
    }

    private fun searchStatisticType(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.matching(it).firstOrNull()
    }

    private fun existsAnotherSameKeyCode(id: String, keyCode: String) =
        StatisticTypeCriteria.anotherSameKeyCodeCriteria(id, keyCode).let {
            statisticTypeRepository.count(it) > 0
        }
}
