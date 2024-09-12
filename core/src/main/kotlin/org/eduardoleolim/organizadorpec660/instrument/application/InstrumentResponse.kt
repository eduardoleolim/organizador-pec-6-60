package org.eduardoleolim.organizadorpec660.instrument.application

import org.eduardoleolim.organizadorpec660.agency.application.SimpleAgencyResponse
import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.instrument.domain.Instrument
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType

class InstrumentResponse(
    val id: String,
    val statisticYear: Int,
    val statisticMonth: Int,
    val savedInSIRESO: Boolean,
    val instrumentFileId: String,
    val agency: SimpleAgencyResponse,
    val statisticType: StatisticTypeResponse,
    val federalEntity: FederalEntityResponse,
    val municipality: SimpleMunicipalityResponse
) {
    companion object {
        fun fromAggregate(
            instrument: Instrument,
            agency: Agency,
            statisticType: StatisticType,
            federalEntity: FederalEntity,
            municipality: Municipality
        ): InstrumentResponse {
            return InstrumentResponse(
                instrument.id().toString(),
                instrument.statisticYear(),
                instrument.statisticMonth(),
                instrument.savedInSIRESO(),
                instrument.instrumentFileId().toString(),
                SimpleAgencyResponse.fromAggregate(agency),
                StatisticTypeResponse.fromAggregate(statisticType),
                FederalEntityResponse.fromAggregate(federalEntity),
                SimpleMunicipalityResponse.fromAggregate(municipality)
            )
        }
    }
}
