/*
 * Copyright (C) 2025 Ángel Eduardo Martínez Leo Lim
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
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentFile
import org.eduardoleolim.organizadorpec660.municipality.application.SimpleMunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType
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
    val filename: String
        get() {
            val stKeyCode = statisticType.keyCode
            val feKeyCode = federalEntity.keyCode
            val formatedYear = statisticYear.toString().takeLast(2).padStart(2, '0')
            val mpKeyCode = municipality.keyCode
            val consecutive = agency.consecutive
            val formatedMonth = statisticMonth.toString().padStart(2, '0')

            return "$stKeyCode$feKeyCode${formatedYear}_$mpKeyCode-${consecutive}_$formatedMonth"
        }

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
