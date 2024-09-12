package org.eduardoleolim.organizadorpec660.agency.application

import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.core.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import java.util.*

class AgencyResponse(
    val id: String,
    val name: String,
    val consecutive: String,
    val municipality: SimpleMunicipalityResponse,
    val statisticTypes: List<StatisticTypeResponse>,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(
            agency: Agency,
            municipality: Municipality,
            statisticTypes: List<StatisticType>
        ): AgencyResponse {
            val statisticTypesResponse = statisticTypes.map { statisticType ->
                StatisticTypeResponse.fromAggregate(statisticType)
            }

            val municipalitiesResponse = SimpleMunicipalityResponse.fromAggregate(municipality)

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
