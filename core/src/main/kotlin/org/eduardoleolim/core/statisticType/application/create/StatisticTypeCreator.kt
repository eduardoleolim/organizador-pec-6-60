package org.eduardoleolim.core.statisticType.application.create

import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.core.statisticType.domain.StatisticType
import org.eduardoleolim.core.statisticType.domain.StatisticTypeAlreadyExistsError
import org.eduardoleolim.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.core.statisticType.domain.StatisticTypeRepository

class StatisticTypeCreator(
    private val statisticTypeRepository: StatisticTypeRepository,
    private val instrumentTypeRepository: InstrumentTypeRepository
) {
    fun create(keyCode: String, name: String, instrumentTypeIds: List<String>) {
        if (existsStatisticType(keyCode))
            throw StatisticTypeAlreadyExistsError(keyCode)

        val filteredInstrumentTypeIds = filterInstrumentTypeIds(instrumentTypeIds)

        StatisticType.create(keyCode, name, filteredInstrumentTypeIds).let {
            statisticTypeRepository.save(it)
        }
    }

    private fun existsStatisticType(keyCode: String) = StatisticTypeCriteria.keyCodeCriteria(keyCode).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun existsInstrumentType(instrumentTypeId: String) =
        InstrumentTypeCriteria.idCriteria(instrumentTypeId).let {
            instrumentTypeRepository.count(it) > 0
        }

    private fun filterInstrumentTypeIds(instrumentTypeIds: List<String>) =
        instrumentTypeIds.distinct().filter { existsInstrumentType(it) }.toMutableList()
}
