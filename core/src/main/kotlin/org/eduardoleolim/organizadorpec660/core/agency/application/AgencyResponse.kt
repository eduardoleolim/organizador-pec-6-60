package org.eduardoleolim.organizadorpec660.core.agency.application

import org.eduardoleolim.organizadorpec660.core.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import java.util.*

class AgencyResponse(
    val id: String,
    val name: String,
    val consecutive: Int,
    val municipality: MunicipalityResponse,
    val statisticTypes: List<StatisticTypeResponse>,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    class MunicipalityResponse(
        val id: String,
        val name: String,
        val keyCode: String,
        val federalEntityId: String,
        val createdAt: Date,
        val updatedAt: Date?
    ) {
        companion object {
            fun fromAggregate(municipality: Municipality) = MunicipalityResponse(
                municipality.id().toString(),
                municipality.name(),
                municipality.keyCode(),
                municipality.federalEntityId().toString(),
                municipality.createdAt(),
                municipality.updatedAt()
            )
        }
    }

    companion object {
        fun fromAggregate(
            agency: Agency,
            municipality: Municipality,
            statisticTypes: List<StatisticType>
        ): AgencyResponse {
            val statisticTypesResponse = statisticTypes.map { statisticType ->
                StatisticTypeResponse.fromAggregate(statisticType)
            }

            val municipalitiesResponse = MunicipalityResponse.fromAggregate(municipality)

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
