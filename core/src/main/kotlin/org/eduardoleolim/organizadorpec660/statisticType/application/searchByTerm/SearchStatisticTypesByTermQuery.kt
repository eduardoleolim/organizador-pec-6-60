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

package org.eduardoleolim.organizadorpec660.statisticType.application.searchByTerm

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError

class SearchStatisticTypesByTermQuery(
    search: String? = null,
    private val orders: Array<HashMap<String, String>>? = null,
    private val limit: Int? = null,
    private val offset: Int? = null
) : Query<StatisticTypeError, StatisticTypesResponse> {
    private val search = search?.trim()?.uppercase()

    fun search(): String? {
        return search
    }

    fun orders(): Array<HashMap<String, String>>? {
        return orders
    }

    fun limit(): Int? {
        return limit
    }

    fun offset(): Int? {
        return offset
    }
}
