package org.eduardoleolim.organizadorpec660.core.instrument.application.create

import org.eduardoleolim.organizadorpec660.core.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFile
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class InstrumentCreator(
    private val instrumentRepository: InstrumentRepository,
    private val instrumentFileRepository: InstrumentFileRepository,
    private val instrumentTypeRepository: InstrumentTypeRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun create(
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        instrumentTypeId: String,
        statisticTypeId: String,
        municipalityId: String,
        file: ByteArray
    ) {
        if (existsInstrumentType(instrumentTypeId).not())
            throw InstrumentTypeNotFoundError(instrumentTypeId)

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
            instrumentTypeId,
            statisticTypeId,
            municipalityId
        )

        instrumentRepository.save(instrument)
        instrumentRepository.save(instrumentFile)
    }

    private fun existsInstrumentType(instrumentTypeId: String) =
        InstrumentTypeCriteria.idCriteria(instrumentTypeId).let {
            instrumentTypeRepository.count(it) > 0
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
