package org.eduardoleolim.organizadorpec660.core.instrument.application.create

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.instrument.domain.*
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeRepository
import java.util.*

class InstrumentCreator(
    private val instrumentRepository: InstrumentRepository,
    private val agencyRepository: AgencyRepository,
    private val statisticTypeRepository: StatisticTypeRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun create(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String,
        file: ByteArray
    ): Either<InstrumentError, UUID> {
        if (existsAgency(agencyId).not())
            return Left(AgencyNotFoundError(agencyId))

        if (existsStatisticType(statisticTypeId).not())
            return Left(StatisticTypeNotFoundError(statisticTypeId))

        if (existsMunicipality(municipalityId).not())
            return Left(MunicipalityNotFoundError(municipalityId))

        val existsInstrument = existsInstrument(
            statisticYear,
            statisticMonth,
            agencyId,
            statisticTypeId,
            municipalityId
        )

        if (existsInstrument)
            return Left(
                InstrumentAlreadyExistsError(
                    statisticYear,
                    statisticMonth,
                    agencyId,
                    statisticTypeId,
                    municipalityId
                )
            )

        val instrumentFile = InstrumentFile.create(file)
        val instrument = Instrument.create(
            statisticYear,
            statisticMonth,
            instrumentFile.id().toString(),
            agencyId,
            statisticTypeId,
            municipalityId
        )

        instrumentRepository.save(instrument, instrumentFile)
        return Right(instrument.id())
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

    private fun existsInstrument(
        statisticYear: Int,
        statisticMonth: Int,
        agencyId: String,
        statisticTypeId: String,
        municipalityId: String
    ) = InstrumentCriteria.otherInstrumentCriteria(
        statisticYear,
        statisticMonth,
        agencyId,
        statisticTypeId,
        municipalityId
    ).let {
        instrumentRepository.count(it) > 0
    }
}
