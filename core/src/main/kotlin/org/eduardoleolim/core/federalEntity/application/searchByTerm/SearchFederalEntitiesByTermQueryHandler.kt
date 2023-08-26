package org.eduardoleolim.core.federalEntity.application.searchByTerm

import org.eduardoleolim.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.shared.domain.criteria.Order

class SearchFederalEntitiesByTermQueryHandler(private val searcher: FederalEntitySearcher) :
    QueryHandler<SearchFederalEntitiesByTermQuery, FederalEntitiesResponse> {
    override fun handle(query: SearchFederalEntitiesByTermQuery): FederalEntitiesResponse {
        val federalEntities = searchFederalEntities(query.search(), query.orders(), query.limit(), query.offset())

        return FederalEntitiesResponse.fromAggregate(federalEntities)
    }

    private fun searchFederalEntities(
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
        searcher.search(it)
    }
}