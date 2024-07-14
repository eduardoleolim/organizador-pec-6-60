package org.eduardoleolim.organizadorpec660.core.agency.application.update

import org.eduardoleolim.organizadorpec660.core.agency.domain.*
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
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
    ) {
        val agency = searchAgency(agencyId) ?: throw AgencyNotFoundError(agencyId)

        if (existsMunicipality(municipalityId).not())
            throw MunicipalityNotFoundError(municipalityId)

        if (statisticTypeIds.isEmpty())
            throw InvalidAgencyStatisticTypesError()

        statisticTypeIds.forEach { statisticTypeId ->
            if (existsStatisticType(statisticTypeId).not())
                throw StatisticTypeNotFoundError(statisticTypeId)
        }

        if (existsAnotherAgencySameConsecutive(agencyId, consecutive, municipalityId))
            throw AgencyAlreadyExistsError(consecutive)

        agency.apply {
            changeName(name)
            changeConsecutive(consecutive)
            changeMunicipalityId(municipalityId)
            replaceStatisticTypeIds(statisticTypeIds)
        }.let {
            agencyRepository.save(agency)
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
