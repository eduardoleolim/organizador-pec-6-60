package org.eduardoleolim.organizadorPec660.core.statisticType.application.searchById

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.QueryHandler
import org.eduardoleolim.organizadorPec660.core.statisticType.application.StatisticTypeResponse
import org.eduardoleolim.organizadorPec660.core.statisticType.application.search.StatisticTypeSearcher
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeCriteria
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticTypeNotFoundError

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
