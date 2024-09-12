package org.eduardoleolim.organizadorpec660.statisticType.application.delete

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.statisticType.domain.*

class StatisticTypeDeleter(
    private val statisticTypeRepository: StatisticTypeRepository,
    private val agencyRepository: AgencyRepository
) {
    fun delete(id: String): Either<StatisticTypeError, Unit> {
        try {
            if (exists(id).not())
                return Left(StatisticTypeNotFoundError(id))

            if (usedInAgencies(id))
                return Left(StatisticTypeUsedInAgency())

            statisticTypeRepository.delete(id)
            return Right(Unit)
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Left(CanNotDeleteStatisticTypeError(e))
        }
    }

    private fun exists(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun usedInAgencies(id: String) = AgencyCriteria.statisticTypeIdCriteria(id).let {
        agencyRepository.count(it) > 0
    }
}
