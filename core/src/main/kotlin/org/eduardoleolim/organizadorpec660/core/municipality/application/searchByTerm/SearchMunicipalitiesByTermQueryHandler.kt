package org.eduardoleolim.organizadorpec660.core.municipality.application.searchByTerm

import org.eduardoleolim.organizadorpec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.organizadorpec660.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Order

class SearchMunicipalitiesByTermQueryHandler(
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher
) : QueryHandler<SearchMunicipalitiesByTermQuery, MunicipalitiesResponse> {
    private val cache = mutableMapOf<String, FederalEntity>()

    override fun handle(query: SearchMunicipalitiesByTermQuery): MunicipalitiesResponse {
        try {
            val municipalities = searchMunicipalities(
                query.federalEntityId(),
                query.search(),
                query.orders(),
                query.limit(),
                query.offset()
            )
            val totalMunicipalities = countTotalMunicipalities(query.federalEntityId(), query.search())

            return municipalities.map { municipality ->
                val federalEntity = searchFederalEntity(municipality.federalEntityId().toString())

                MunicipalityResponse.fromAggregate(municipality, federalEntity)
            }.let {
                MunicipalitiesResponse(it, totalMunicipalities, query.limit(), query.offset())
            }
        } finally {
            cache.clear()
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
        orders = orders?.map { Order.fromValues(it["orderBy"], it["orderType"]) }?.toMutableList(),
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

    private fun searchFederalEntity(id: String): FederalEntity {
        if (cache.containsKey(id)) {
            return cache[id]!!
        }

        val criteria = FederalEntityCriteria.idCriteria(id)

        return this.federalEntitySearcher.search(criteria).first().let {
            cache[id] = it
            it
        }
    }
}
