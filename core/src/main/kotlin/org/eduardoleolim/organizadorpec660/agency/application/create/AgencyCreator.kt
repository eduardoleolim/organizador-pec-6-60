package org.eduardoleolim.organizadorpec660.agency.application.create

import org.eduardoleolim.organizadorpec660.agency.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository
import java.util.*

class AgencyCreator(
    private val agencyRepository: AgencyRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val statisticTypeRepository: StatisticTypeRepository
) {
    fun create(
        name: String,
        consecutive: String,
        municipalityId: String,
        statisticTypeIds: List<String>
    ): Either<AgencyError, UUID> {
        try {
            if (existsMunicipality(municipalityId).not())
                return Left(MunicipalityNotFoundError(municipalityId))

            if (statisticTypeIds.isEmpty())
                return Left(AgencyHasNoStatisticTypesError())

            statisticTypeIds.forEach { statisticTypeId ->
                if (existsStatisticType(statisticTypeId).not())
                    return Left(StatisticTypeNotFoundError(statisticTypeId))
            }

            if (existsAnotherAgencySameConsecutive(consecutive, municipalityId))
                return Left(AgencyAlreadyExistsError(consecutive))

            return Agency.create(name, consecutive, municipalityId, statisticTypeIds).let {
                agencyRepository.save(it)
                Right(it.id())
            }
        } catch (e: InvalidArgumentAgencyException) {
            return Left(CanNotSaveAgencyError(e))
        }
    }

    private fun existsMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun existsAnotherAgencySameConsecutive(consecutive: String, municipalityId: String) =
        AgencyCriteria.anotherConsecutiveCriteria(consecutive, municipalityId).let {
            agencyRepository.count(it) > 0
        }
}
