package org.eduardoleolim.organizadorpec660.core.instrument.application

import org.eduardoleolim.organizadorpec660.core.agency.application.SimpleAgencyResponse
import org.eduardoleolim.organizadorpec660.core.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.core.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentFile
import org.eduardoleolim.organizadorpec660.core.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import java.util.*

class DetailedInstrumentResponse(
    val id: String,
    val statisticYear: Int,
    val statisticMonth: Int,
    val saved: Boolean,
    val instrumentFile: InstrumentFileResponse,
    val municipality: SimpleMunicipalityResponse,
    val federalEntity: FederalEntityResponse,
    val agency: SimpleAgencyResponse,
    val statisticType: StatisticTypeResponse,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(
            instrument: Instrument,
            instrumentFile: InstrumentFile,
            municipality: Municipality,
            federalEntity: FederalEntity,
            agency: Agency,
            statisticType: StatisticType
        ): DetailedInstrumentResponse {
            return DetailedInstrumentResponse(
                instrument.id().toString(),
                instrument.statisticYear(),
                instrument.statisticMonth(),
                instrument.savedInSIRESO(),
                InstrumentFileResponse.fromAggregate(instrumentFile),
                SimpleMunicipalityResponse.fromAggregate(municipality),
                FederalEntityResponse.fromAggregate(federalEntity),
                SimpleAgencyResponse.fromAggregate(agency),
                StatisticTypeResponse.fromAggregate(statisticType),
                instrument.createdAt(),
                instrument.updatedAt()
            )
        }
    }
}