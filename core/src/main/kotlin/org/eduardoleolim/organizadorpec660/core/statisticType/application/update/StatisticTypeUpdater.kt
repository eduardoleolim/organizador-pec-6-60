package org.eduardoleolim.organizadorpec660.core.statisticType.application.update

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class StatisticTypeUpdater(
    private val statisticTypeRepository: StatisticTypeRepository,
    private val instrumentTypeRepository: StatisticTypeRepository
) {
    fun update(id: String, keyCode: String, name: String, instrumentTypeIds: List<String>) {
        val statisticType = searchStatisticType(id) ?: throw StatisticTypeNotFoundError(id)

        if (existsAnotherSameKeyCode(id, keyCode))
            throw StatisticTypeAlreadyExistsError(keyCode)

        val filteredInstrumentTypeIds = filterInstrumentTypeIds(instrumentTypeIds)

        statisticType.apply {
            changeKeyCode(keyCode)
            changeName(name)
            changeInstrumentTypeIds(filteredInstrumentTypeIds)
        }.let {
            statisticTypeRepository.save(it)
        }
    }

    private fun searchStatisticType(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.matching(it).firstOrNull()
    }

    private fun existsInstrumentType(instrumentTypeId: String) =
        InstrumentTypeCriteria.idCriteria(instrumentTypeId).let {
            instrumentTypeRepository.count(it) > 0
        }

    private fun existsAnotherSameKeyCode(id: String, keyCode: String) =
        StatisticTypeCriteria.anotherSameKeyCodeCriteria(id, keyCode).let {
            statisticTypeRepository.count(it) > 0
        }

    private fun filterInstrumentTypeIds(instrumentTypeIds: List<String>) =
        instrumentTypeIds.distinct().filter { existsInstrumentType(it) }.toMutableList()
}
