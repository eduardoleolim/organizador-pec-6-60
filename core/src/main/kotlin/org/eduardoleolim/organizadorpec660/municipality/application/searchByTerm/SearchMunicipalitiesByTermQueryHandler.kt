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

package org.eduardoleolim.organizadorpec660.municipality.application.searchByTerm

import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler

class SearchMunicipalitiesByTermQueryHandler(
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher
) : QueryHandler<SearchMunicipalitiesByTermQuery, MunicipalitiesResponse> {
    private val federalEntityCache = mutableMapOf<String, FederalEntity>()

    override fun handle(query: SearchMunicipalitiesByTermQuery): MunicipalitiesResponse {
        val municipalities = searchMunicipalities(
            query.federalEntityId(),
            query.search(),
            query.orders(),
            query.limit(),
            query.offset()
        )
        val totalMunicipalities = countTotalMunicipalities(query.federalEntityId(), query.search())

        return municipalities.map { municipality ->
            val federalEntityId = municipality.federalEntityId().toString()
            val federalEntity = federalEntityCache[federalEntityId] ?: searchFederalEntity(federalEntityId).also {
                federalEntityCache[federalEntityId] = it
            }

            MunicipalityResponse.fromAggregate(municipality, federalEntity)
        }.let {
            federalEntityCache.clear()

            MunicipalitiesResponse(it, totalMunicipalities, query.limit(), query.offset())
        }
    }

    private fun searchMunicipalities(
        federalEntityId: String? = null,
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = MunicipalityCriteria.searchCriteria(
        federalEntityId = federalEntityId,
        search = search,
        orders = orders,
        limit = limit,
        offset = offset
    ).let {
        municipalitySearcher.search(it)
    }

    private fun countTotalMunicipalities(
        federalEntityId: String? = null,
        search: String? = null
    ) = MunicipalityCriteria.searchCriteria(
        federalEntityId = federalEntityId,
        search = search
    ).let {
        municipalitySearcher.count(it)
    }

    private fun searchFederalEntity(id: String) = FederalEntityCriteria.idCriteria(id).let {
        federalEntitySearcher.search(it).first()
    }
}
