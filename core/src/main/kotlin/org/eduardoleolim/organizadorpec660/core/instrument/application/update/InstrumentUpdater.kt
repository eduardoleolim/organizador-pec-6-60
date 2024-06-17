package org.eduardoleolim.organizadorpec660.core.instrument.application.update

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository

class InstrumentUpdater(
    private val instrumentRepository: InstrumentRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun update(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        statisticTypeId: String,
        municipalityId: String
    ) {
        val instrument = searchInstrument(instrumentId) ?: throw InstrumentNotFoundError(instrumentId)

        val existsAnotherInstrument = existsAnotherInstrumentSameData(
            instrumentId,
            statisticYear,
            statisticMonth,
            consecutive,
            statisticTypeId,
            municipalityId
        )

        if (existsAnotherInstrument)
            throw InstrumentAlreadyExistsError(
                statisticYear,
                statisticMonth,
                consecutive,
                statisticTypeId,
                municipalityId
            )

        if (existsStatisticType(statisticTypeId).not())
            throw StatisticTypeNotFoundError(statisticTypeId)

        if (existsMunicipality(municipalityId).not())
            throw MunicipalityNotFoundError(municipalityId)

        instrument.changeStatisticYear(statisticYear)
        instrument.changeStatisticMonth(statisticMonth)
        instrument.changeConsecutive(consecutive)
        instrument.changeStatisticTypeId(statisticTypeId)
        instrument.changeMunicipalityId(municipalityId)

        instrumentRepository.save(instrument)
    }

    private fun searchInstrument(instrumentId: String) =
        InstrumentCriteria.idCriteria(instrumentId).let {
            instrumentRepository.matching(it).firstOrNull()
        }

    private fun existsAnotherInstrumentSameData(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        consecutive: String,
        statisticTypeId: String,
        municipalityId: String
    ) =
        InstrumentCriteria.anotherInstrumentCriteria(
            instrumentId,
            statisticYear,
            statisticMonth,
            consecutive,
            statisticTypeId,
            municipalityId
        ).let {
            instrumentRepository.count(it) > 0
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
