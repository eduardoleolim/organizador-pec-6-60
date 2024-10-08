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
