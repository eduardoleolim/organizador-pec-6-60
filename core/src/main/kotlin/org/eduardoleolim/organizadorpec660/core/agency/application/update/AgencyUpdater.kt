package org.eduardoleolim.organizadorpec660.core.agency.application.update

import org.eduardoleolim.organizadorpec660.core.agency.domain.*
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class AgencyUpdater(
    private val agencyRepository: AgencyRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val statisticTypeRepository: StatisticTypeRepository
) {
    fun update(
        agencyId: String,
        name: String,
        consecutive: String,
        municipalityId: String,
        statisticTypeIds: List<String>
    ): Either<AgencyError, Unit> {
        try {
            val agency = searchAgency(agencyId) ?: return Left(AgencyNotFoundError(agencyId))

            if (statisticTypeIds.isEmpty())
                return Left(AgencyHasNoStatisticTypesError())

            if (existsMunicipality(municipalityId).not())
                return Left(MunicipalityNotFoundError(municipalityId))

            statisticTypeIds.forEach { statisticTypeId ->
                if (existsStatisticType(statisticTypeId).not())
                    return Left(StatisticTypeNotFoundError(statisticTypeId))
            }

            if (existsAnotherAgencySameConsecutive(agencyId, consecutive, municipalityId))
                return Left(AgencyAlreadyExistsError(consecutive))

            agency.apply {
                changeName(name)
                changeConsecutive(consecutive)
                changeMunicipalityId(municipalityId)
                replaceStatisticTypeIds(statisticTypeIds)
            }.let {
                agencyRepository.save(agency)
                return Right(Unit)
            }
        } catch (e: InvalidArgumentAgencyException) {
            return Left(CanNotSaveAgencyError(e))
        }
    }

    private fun searchAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencyRepository.matching(it).firstOrNull()
    }

    private fun existsMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun existsAnotherAgencySameConsecutive(agencyId: String, consecutive: String, municipalityId: String) =
        AgencyCriteria.anotherConsecutiveCriteria(agencyId, consecutive, municipalityId).let {
            agencyRepository.count(it) > 0
        }
}
