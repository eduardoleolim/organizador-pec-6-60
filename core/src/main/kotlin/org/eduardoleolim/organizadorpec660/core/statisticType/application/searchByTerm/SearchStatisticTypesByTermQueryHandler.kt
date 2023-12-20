package org.eduardoleolim.organizadorpec660.core.statisticType.application.searchByTerm

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Order
import org.eduardoleolim.organizadorpec660.core.statisticType.application.StatisticTypesResponse
import org.eduardoleolim.organizadorpec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeCriteria

class SearchStatisticTypesByTermQueryHandler(private val searcher: StatisticTypeSearcher) :
    QueryHandler<SearchStatisticTypesByTermQuery, StatisticTypesResponse> {
    override fun handle(query: SearchStatisticTypesByTermQuery): StatisticTypesResponse {
        val statisticTypes = searchStatisticTypes(query.search(), query.orders(), query.limit(), query.offset())
        val totalStatisticTypes = countStatisticTypes(query.search())

        return StatisticTypesResponse.fromAggregate(statisticTypes, totalStatisticTypes)
    }

    private fun searchStatisticTypes(
        search: String? = null,
        orders: Array<HashMap<String, String>>? = null,
        limit: Int? = null,
        offset: Int? = null
    ) = StatisticTypeCriteria.searchCriteria(
        search = search,
        orders = orders?.map { Order.fromValues(it["orderBy"], it["orderType"]) }?.toMutableList(),
        limit = limit,
        offset = offset
    ).let {
        searcher.search(it)
    }

    private fun countStatisticTypes(search: String? = null) = StatisticTypeCriteria.searchCriteria(
        search = search
    ).let {
        searcher.count(it)
    }
}
