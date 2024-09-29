package org.eduardoleolim.organizadorpec660.statisticType.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.statisticType.domain.*

class StatisticTypeDeleter(
    private val statisticTypeRepository: StatisticTypeRepository,
    private val agencyRepository: AgencyRepository
) {
    fun delete(id: String): Either<StatisticTypeError, Unit> {
        try {
            if (exists(id).not())
                return Either.Left(StatisticTypeNotFoundError(id))

            if (usedInAgencies(id))
                return Either.Left(StatisticTypeUsedInAgency())

            statisticTypeRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentStatisticTypeException) {
            return Either.Left(CanNotDeleteStatisticTypeError(e))
        }
    }

    private fun exists(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun usedInAgencies(id: String) = AgencyCriteria.statisticTypeIdCriteria(id).let {
        agencyRepository.count(it) > 0
    }
}
