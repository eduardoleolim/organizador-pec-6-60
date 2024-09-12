package org.eduardoleolim.organizadorpec660.core.instrument.application.update

import org.eduardoleolim.organizadorpec660.core.instrument.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeRepository

class InstrumentUpdater(
    private val instrumentRepository: InstrumentRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun update(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String,
        file: ByteArray
    ): Either<InstrumentError, Unit> {
        val instrument = searchInstrument(instrumentId) ?: return Left(InstrumentNotFoundError(instrumentId))
        val instrumentFileId = instrument.instrumentFileId().toString()
        val instrumentFile =
            searchInstrumentFile(instrumentFileId) ?: return Left(InstrumentFileNotFoundError(instrumentFileId))

        val existsAnotherInstrument = existsAnotherInstrumentSameData(
            instrumentId,
            statisticYear,
            statisticMonth,
            agencyId,
            statisticTypeId,
            municipalityId
        )

        if (existsAnotherInstrument)
            return Left(
                InstrumentAlreadyExistsError(
                    statisticYear,
                    statisticMonth,
                    agencyId,
                    statisticTypeId,
                    municipalityId
                )
            )

        if (existsStatisticType(statisticTypeId).not())
            return Left(StatisticTypeNotFoundError(statisticTypeId))

        if (existsMunicipality(municipalityId).not())
            return Left(MunicipalityNotFoundError(municipalityId))

        instrument.apply {
            changeStatisticYear(statisticYear)
            changeStatisticMonth(statisticMonth)
            changeStatisticTypeId(statisticTypeId)
            changeMunicipalityId(municipalityId)
        }

        instrumentFile.apply {
            changeContent(file)
        }

        instrumentRepository.save(instrument, instrumentFile)

        return Right(Unit)
    }

    private fun searchInstrument(instrumentId: String) =
        InstrumentCriteria.idCriteria(instrumentId).let {
            instrumentRepository.matching(it).firstOrNull()
        }

    private fun searchInstrumentFile(instrumentFileId: String) =
        instrumentRepository.searchInstrumentFile(instrumentFileId)

    private fun existsAnotherInstrumentSameData(
        instrumentId: String,
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = InstrumentCriteria.anotherInstrumentCriteria(
        instrumentId,
        statisticYear,
        statisticMonth,
        agencyId,
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
