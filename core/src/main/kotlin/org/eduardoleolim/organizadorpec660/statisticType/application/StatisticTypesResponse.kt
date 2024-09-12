package org.eduardoleolim.organizadorpec660.statisticType.application

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticType

class StatisticTypesResponse(
    val statisticTypes: List<StatisticTypeResponse>,
    val total: Int,
    val limit: Int?,
    val offset: Int?
) : Response {
    val filtered: Int
        get() = statisticTypes.size

    companion object {
        fun fromAggregate(statisticTypes: List<StatisticType>, total: Int, limit: Int?, offset: Int?) =
            StatisticTypesResponse(statisticTypes.map(StatisticTypeResponse.Companion::fromAggregate), total, limit, offset)
    }
}
