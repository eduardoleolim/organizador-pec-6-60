package org.eduardoleolim.organizadorpec660.core.statisticType.application

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType

class StatisticTypesResponse(
    val statisticTypes: List<StatisticTypeResponse>,
    val totalRecords: Int
) : Response {
    companion object {
        fun fromAggregate(statisticTypes: List<StatisticType>, totalRecords: Int) =
            StatisticTypesResponse(statisticTypes.map(StatisticTypeResponse::fromAggregate), totalRecords)
    }
}
