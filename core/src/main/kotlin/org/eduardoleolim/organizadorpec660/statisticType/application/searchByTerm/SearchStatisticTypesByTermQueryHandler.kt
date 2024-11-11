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

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError

class SearchStatisticTypesByTermQueryHandler(private val searcher: StatisticTypeSearcher) :
    QueryHandler<StatisticTypeError, StatisticTypesResponse, SearchStatisticTypesByTermQuery> {
    override fun handle(query: SearchStatisticTypesByTermQuery): Either<StatisticTypeError, StatisticTypesResponse> {
        val statisticTypes = searchStatisticTypes(query.search(), query.orders(), query.limit(), query.offset())
        val totalStatisticTypes = countStatisticTypes(query.search())

        return Either.Right(
            StatisticTypesResponse.fromAggregate(
                statisticTypes,
                totalStatisticTypes,
                query.limit(),
                query.offset()
            )
        )
    }

    private fun searchStatisticTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = StatisticTypeCriteria.searchCriteria(
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        searcher.search(it)
    }

    private fun countStatisticTypes(search: String? = null) = StatisticTypeCriteria.searchCriteria(
        search = search
    ).let {
        searcher.count(it)
    }
}
