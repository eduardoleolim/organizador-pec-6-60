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

package org.eduardoleolim.organizadorpec660.statisticType.application.searchById

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeNotFoundError

class SearchStatisticTypeByIdQueryHandler(private val searcher: StatisticTypeSearcher) :
    QueryHandler<StatisticTypeError, StatisticTypeResponse, SearchStatisticTypeByIdQuery> {
    override fun handle(query: SearchStatisticTypeByIdQuery): Either<StatisticTypeError, StatisticTypeResponse> {
        val statisticType = searchStatisticType(query.id()) ?: throw StatisticTypeNotFoundError(query.id())

        return Either.Right(StatisticTypeResponse.fromAggregate(statisticType))
    }

    private fun searchStatisticType(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        searcher.search(it).firstOrNull()
    }
}
