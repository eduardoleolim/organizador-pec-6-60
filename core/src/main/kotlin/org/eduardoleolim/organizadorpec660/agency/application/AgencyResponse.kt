/*
 * Copyright (C) 2024 Ángel Eduardo Martínez Leo Lim
 * This file is part of organizador-pec-6-60.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eduardoleolim.organizadorpec660.agency.application

import org.eduardoleolim.organizadorpec660.agency.domain.Agency
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType
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
