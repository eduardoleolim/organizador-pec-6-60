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

package org.eduardoleolim.organizadorpec660.instrument.model

import org.eduardoleolim.organizadorpec660.agency.application.AgencyResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse

data class InstrumentSearchParameters(
    val search: String = "",
    val statisticYear: Int? = null,
    val statisticMonth: Pair<Int, String>? = null,
    val statisticType: StatisticTypeResponse? = null,
    val federalEntity: FederalEntityResponse? = null,
    val municipality: MunicipalityResponse? = null,
    val agency: AgencyResponse? = null,
    val orders: List<HashMap<String, String>> = emptyList(),
    val limit: Int? = null,
    val offset: Int? = null
)
