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

package org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler

class SearchFederalEntitiesByTermQueryHandler(private val searcher: FederalEntitySearcher) :
    QueryHandler<FederalEntityError, FederalEntitiesResponse, SearchFederalEntitiesByTermQuery> {
    override fun handle(query: SearchFederalEntitiesByTermQuery): Either<FederalEntityError, FederalEntitiesResponse> {
        val federalEntities = searchFederalEntities(query.search(), query.orders(), query.limit(), query.offset())
        val totalFederalEntities = countTotalFederalEntities(query.search())

        return Either.Right(
            FederalEntitiesResponse.fromAggregate(
                federalEntities,
                totalFederalEntities,
                query.limit(),
                query.offset()
            )
        )
    }

    private fun searchFederalEntities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = FederalEntityCriteria.searchCriteria(
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        searcher.search(it)
    }

    private fun countTotalFederalEntities(search: String? = null) = FederalEntityCriteria.searchCriteria(
        search = search
    ).let {
        searcher.count(it)
    }
}
