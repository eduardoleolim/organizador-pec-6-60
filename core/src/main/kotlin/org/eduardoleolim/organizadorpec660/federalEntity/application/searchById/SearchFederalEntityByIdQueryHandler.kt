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

package org.eduardoleolim.organizadorpec660.federalEntity.application.searchById

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntityResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler

class SearchFederalEntityByIdQueryHandler(private val searcher: FederalEntitySearcher) :
    QueryHandler<FederalEntityError, FederalEntityResponse, SearchFederalEntityByIdQuery> {
    override fun handle(query: SearchFederalEntityByIdQuery): Either<FederalEntityError, FederalEntityResponse> {
        val federalEntity = searchFederalEntity(query.id())
            ?: return Either.Left(FederalEntityNotFoundError(query.id()))

        return Either.Right(FederalEntityResponse.fromAggregate(federalEntity))
    }

    private fun searchFederalEntity(id: String) = FederalEntityCriteria.idCriteria(id).let {
        searcher.search(it).firstOrNull()
    }
}
