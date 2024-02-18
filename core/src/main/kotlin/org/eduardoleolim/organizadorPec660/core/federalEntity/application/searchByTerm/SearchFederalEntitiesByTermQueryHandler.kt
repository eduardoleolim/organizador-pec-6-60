package org.eduardoleolim.organizadorPec660.core.federalEntity.application.searchByTerm

import org.eduardoleolim.organizadorPec660.core.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorPec660.core.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Order

class SearchFederalEntitiesByTermQueryHandler(private val searcher: FederalEntitySearcher) :
    QueryHandler<SearchFederalEntitiesByTermQuery, FederalEntitiesResponse> {
    override fun handle(query: SearchFederalEntitiesByTermQuery): FederalEntitiesResponse {
        val federalEntities = searchFederalEntities(query.search(), query.orders(), query.limit(), query.offset())
        val totalFederalEntities = countTotalFederalEntities(query.search())

        return FederalEntitiesResponse.fromAggregate(
            federalEntities,
            totalFederalEntities,
            query.limit(),
            query.offset()
        )
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

    private fun countTotalFederalEntities(search: String? = null) = FederalEntityCriteria.searchCriteria(
        search = search
    ).let {
        searcher.count(it)
    }
}
