package org.eduardoleolim.organizadorpec660.federalEntity.application.searchByTerm

import org.eduardoleolim.organizadorpec660.federalEntity.application.FederalEntitiesResponse
import org.eduardoleolim.organizadorpec660.federalEntity.application.search.FederalEntitySearcher
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.QueryHandler

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
