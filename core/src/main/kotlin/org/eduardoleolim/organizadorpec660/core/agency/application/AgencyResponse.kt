package org.eduardoleolim.organizadorpec660.core.agency.application

import org.eduardoleolim.organizadorpec660.core.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.core.instrumentType.application.InstrumentTypeResponse
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import java.util.*

class AgencyResponse(
    val id: String,
    val name: String,
    val consecutive: Int,
    val municipalities: List<Pair<MunicipalityResponse, Boolean>>,
    val statisticTypes: List<Pair<StatisticTypeResponse, InstrumentTypeResponse>>,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    class MunicipalityResponse(
        val id: String,
        val name: String,
        val keyCode: String,
        val createdAt: Date,
        val updatedAt: Date?
    ) {
        companion object {
            fun fromAggregate(municipality: Municipality) = MunicipalityResponse(
                municipality.id().toString(),
                municipality.name(),
                municipality.keyCode(),
                municipality.createdAt(),
                municipality.updatedAt()
            )
        }
    }

    companion object {
        fun fromAggregate(
            agency: Agency,
            municipalities: List<Pair<Municipality, Boolean>>,
            statisticTypes: List<Pair<StatisticType, InstrumentType>>
        ): AgencyResponse {
            val statisticTypesResponse = statisticTypes.map { statisticTypeAssociation ->
                Pair(
                    StatisticTypeResponse.fromAggregate(statisticTypeAssociation.first),
                    InstrumentTypeResponse.fromAggregate(statisticTypeAssociation.second)
                )
            }

            val municipalitiesResponse = municipalities.map { municipalityAssociation ->
                Pair(
                    MunicipalityResponse.fromAggregate(municipalityAssociation.first),
                    municipalityAssociation.second
                )
            }

            return AgencyResponse(
                agency.id().toString(),
                agency.name(),
                agency.consecutive(),
                municipalitiesResponse,
                statisticTypesResponse,
                agency.createdAt(),
                agency.updatedAt()
            )
        }
    }
}
