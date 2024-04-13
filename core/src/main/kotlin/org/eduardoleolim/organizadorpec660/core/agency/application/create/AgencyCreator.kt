package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.agency.domain.*
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class AgencyCreator(
    private val agencyRepository: AgencyRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val instrumentTypeRepository: InstrumentTypeRepository
) {
    fun create(
        name: String,
        consecutive: Int,
        municipalities: List<Pair<String, Boolean>>,
        statisticTypes: List<Pair<String, String>>
    ) {
        if (municipalities.isEmpty())
            throw InvalidAgencyMunicipalitiesError()

        if (statisticTypes.isEmpty())
            throw InvalidAgencyStatisticTypesError()

        municipalities.forEach {
            if (existsMunicipality(it.first).not())
                throw MunicipalityNotFoundError(it.first)
        }

        statisticTypes.forEach {
            if (existsStatisticType(it.first).not())
                throw StatisticTypeNotFoundError(it.first)

            if (existsInstrumentType(it.second).not())
                throw InstrumentTypeNotFoundError(it.second)
        }

        val belongJustOne = municipalities.count { it.second }
        if (belongJustOne != 1)
            throw InvalidAgencyMunicipalityOwnerError()

        val municipalityOwnerId = municipalities.first { it.second }.first

        if (existsAnotherAgencySameConsecutive(consecutive, municipalityOwnerId))
            throw AgencyAlreadyExists(consecutive)

        Agency.create(name, consecutive, municipalities, statisticTypes).let {
            agencyRepository.save(it)
        }
    }

    private fun existsMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) = StatisticTypeCriteria.idCriteria(statisticTypeId).let {
        statisticTypeRepository.count(it) > 0
    }

    private fun existsInstrumentType(instrumentTypeId: String) =
        InstrumentTypeCriteria.idCriteria(instrumentTypeId).let {
            instrumentTypeRepository.count(it) > 0
        }

    private fun existsAnotherAgencySameConsecutive(consecutive: Int, municipalityOwnerId: String) =
        AgencyCriteria.anotherConsecutive(consecutive, municipalityOwnerId).let {
            municipalityRepository.count(it) > 0
        }
}
