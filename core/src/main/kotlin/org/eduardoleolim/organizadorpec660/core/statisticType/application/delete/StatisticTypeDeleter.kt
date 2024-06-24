package org.eduardoleolim.organizadorpec660.core.statisticType.application.delete

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeUsedinAgency

class StatisticTypeDeleter(
    private val statisticTypeRepository: StatisticTypeRepository,
    private val agencyRepository: AgencyRepository
) {
    fun delete(id: String) {
        if (exists(id).not())
            throw StatisticTypeNotFoundError(id)

        if (usedInAgencies(id))
            throw StatisticTypeUsedinAgency()

        statisticTypeRepository.delete(id)
    }

    private fun exists(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun usedInAgencies(id: String) = AgencyCriteria.statisticTypeCriteria(id).let {
        agencyRepository.count(it) > 0
    }
}
