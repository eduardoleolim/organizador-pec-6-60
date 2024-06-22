package org.eduardoleolim.organizadorpec660.core.instrument.application.create

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFile
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFileRepository
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class InstrumentCreator(
    private val instrumentRepository: InstrumentRepository,
    private val instrumentFileRepository: InstrumentFileRepository,
    private val agencyRepository: AgencyRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun create(
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String,
        file: ByteArray
    ) {
        if (existsAgency(agencyId).not())
            throw AgencyNotFoundError(agencyId)

        if (existsStatisticType(statisticTypeId).not())
            throw StatisticTypeNotFoundError(statisticTypeId)

        if (existsMunicipality(municipalityId).not())
            throw MunicipalityNotFoundError(municipalityId)

        val instrumentFile = InstrumentFile.create(file)
        val instrument = Instrument.create(
            statisticYear,
            statisticMonth,
            consecutive,
            instrumentFile.id().toString(),
            agencyId,
            statisticTypeId,
            municipalityId
        )

        instrumentRepository.save(instrument)
        instrumentFileRepository.save(instrumentFile)
    }

    private fun existsAgency(agencyId: String) = AgencyCriteria.idCriteria(agencyId).let {
        agencyRepository.count(it) > 0
    }

    private fun existsStatisticType(statisticTypeId: String) =
        StatisticTypeCriteria.idCriteria(statisticTypeId).let {
            statisticTypeRepository.count(it) > 0
        }

    private fun existsMunicipality(municipalityId: String) =
        MunicipalityCriteria.idCriteria(municipalityId).let {
            municipalityRepository.count(it) > 0
        }
}
