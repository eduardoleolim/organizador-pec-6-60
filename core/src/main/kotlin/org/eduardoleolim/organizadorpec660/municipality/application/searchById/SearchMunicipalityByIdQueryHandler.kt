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

package org.eduardoleolim.organizadorpec660.municipality.application.searchById

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler

class SearchMunicipalityByIdQueryHandler(
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher
) : QueryHandler<MunicipalityError, MunicipalityResponse, SearchMunicipalityByIdQuery> {
    override fun handle(query: SearchMunicipalityByIdQuery): Either<MunicipalityError, MunicipalityResponse> {
        val municipality = searchMunicipality(query.id()) ?: throw MunicipalityNotFoundError(query.id())
        val federalEntity = searchFederalEntity(municipality.federalEntityId().toString())

        return Either.Right(MunicipalityResponse.fromAggregate(municipality, federalEntity))
    }

    private fun searchMunicipality(id: String) = MunicipalityCriteria.idCriteria(id).let {
        municipalitySearcher.search(it).firstOrNull()
    }

    private fun searchFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntitySearcher.search(it).first() // If the municipality exists, the federal entity must exists too.
    }
}
