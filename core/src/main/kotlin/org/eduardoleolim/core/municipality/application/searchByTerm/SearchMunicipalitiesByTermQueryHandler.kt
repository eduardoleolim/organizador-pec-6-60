package org.eduardoleolim.core.municipality.application.searchByTerm

import org.eduardoleolim.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.municipality.application.MunicipalitiesResponse
import org.eduardoleolim.core.municipality.application.MunicipalityResponse
import org.eduardoleolim.core.municipality.application.search.MunicipalitySearcher
import org.eduardoleolim.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.shared.domain.criteria.Order

class SearchMunicipalitiesByTermQueryHandler(
    private val municipalitySearcher: MunicipalitySearcher,
    private val federalEntitySearcher: FederalEntitySearcher
) : QueryHandler<SearchMunicipalitiesByTermQuery, MunicipalitiesResponse> {
    private val cache = mutableMapOf<String, FederalEntity>()

    override fun handle(query: SearchMunicipalitiesByTermQuery): MunicipalitiesResponse {
        val municipalities = searchMunicipalities(query.search(), query.orders(), query.limit(), query.offset())

        return municipalities.map { municipality ->
            searchFederalEntity(municipality.federalEntityId().toString()).let { federalEntity ->
                MunicipalityResponse.fromAggregate(municipality, federalEntity)
            }
        }.let {
            cache.clear()
            MunicipalitiesResponse(it)
        }
    }

    private fun searchMunicipalities(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = FederalEntityCriteria.searchCriteria(
        search = search,
        orders = orders?.map { Order.fromValues(it["orderBy"], it["orderType"]) }?.toMutableList(),
        limit = limit,
        offset = offset
    ).let {
        municipalitySearcher.search(it)
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
