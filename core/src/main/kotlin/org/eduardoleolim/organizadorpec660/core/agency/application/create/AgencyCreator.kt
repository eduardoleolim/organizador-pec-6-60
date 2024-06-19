package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.agency.domain.*
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

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
    ) {
        if (existsMunicipality(municipalityId).not())
            throw MunicipalityNotFoundError(municipalityId)

        if (statisticTypeIds.isEmpty())
            throw InvalidAgencyStatisticTypesError()

        statisticTypeIds.forEach { statisticTypeId ->
            if (existsStatisticType(statisticTypeId).not())
                throw StatisticTypeNotFoundError(statisticTypeId)
        }

        if (existsAnotherAgencySameConsecutive(consecutive, municipalityId))
            throw AgencyAlreadyExists(consecutive)

        Agency.create(name, consecutive, municipalityId, statisticTypeIds).let {
            agencyRepository.save(it)
        }
    }

    private fun existsMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun existsAnotherAgencySameConsecutive(consecutive: String, municipalityId: String) =
        AgencyCriteria.anotherConsecutive(consecutive, municipalityId).let {
            agencyRepository.count(it) > 0
        }
}
