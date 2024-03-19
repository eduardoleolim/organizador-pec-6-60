package org.eduardoleolim.organizadorPec660.core.statisticType.application

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorPec660.core.statisticType.domain.StatisticType

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
            StatisticTypesResponse(statisticTypes.map(StatisticTypeResponse::fromAggregate), total, limit, offset)
    }
}
