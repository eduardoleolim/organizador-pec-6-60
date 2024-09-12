package org.eduardoleolim.organizadorpec660.statisticType.application.searchById

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorpec660.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorpec660.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeNotFoundError

class SearchStatisticTypeByIdQueryHandler(private val searcher: StatisticTypeSearcher) :
    QueryHandler<SearchStatisticTypeByIdQuery, StatisticTypeResponse> {
    override fun handle(query: SearchStatisticTypeByIdQuery): StatisticTypeResponse {
        val statisticType = searchStatisticType(query.id()) ?: throw StatisticTypeNotFoundError(query.id())

        return StatisticTypeResponse.fromAggregate(statisticType)
    }

    private fun searchStatisticType(id: String) = StatisticTypeCriteria.idCriteria(id).let {
        searcher.search(it).firstOrNull()
    }
}
