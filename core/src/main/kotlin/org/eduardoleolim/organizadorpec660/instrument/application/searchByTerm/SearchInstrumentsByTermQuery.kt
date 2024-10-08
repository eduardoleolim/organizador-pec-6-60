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

package org.eduardoleolim.organizadorpec660.instrument.application.searchByTerm

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class SearchInstrumentsByTermQuery(
    federalEntityId: String? = null,
    municipalityId: String? = null,
    agencyId: String? = null,
    statisticTypeId: String? = null,
    private val year: Int? = null,
    private val month: Int? = null,
    search: String? = null,
    private val orders: Array<HashMap<String, String>>? = null,
    private val limit: Int? = null,
    private val offset: Int? = null
) : Query {
    private val federalEntityId = federalEntityId?.trim()
    private val municipalityId = municipalityId?.trim()
    private val agencyId = agencyId?.trim()
    private val statisticTypeId = statisticTypeId?.trim()
    private val search = search?.trim()?.uppercase()

    fun federalEntityId(): String? {
        return federalEntityId
    }

    fun municipalityId(): String? {
        return municipalityId
    }

    fun agencyId(): String? {
        return agencyId
    }

    fun statisticTypeId(): String? {
        return statisticTypeId
    }

    fun year(): Int? {
        return year
    }

    fun month(): Int? {
        return month
    }

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
