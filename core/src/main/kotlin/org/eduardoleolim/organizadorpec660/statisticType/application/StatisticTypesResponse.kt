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

package org.eduardoleolim.organizadorpec660.statisticType.application

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType

class StatisticTypesResponse(
    val statisticTypes: List<StatisticTypeResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = statisticTypes.size

    companion object {
        fun fromAggregate(statisticTypes: List<StatisticType>, total: Int, limit: Int?, offset: Int?) =
            StatisticTypesResponse(
                statisticTypes.map(StatisticTypeResponse.Companion::fromAggregate),
                total,
                limit,
                offset
            )
    }
}
