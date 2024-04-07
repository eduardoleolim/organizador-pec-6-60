package org.eduardoleolim.organizadorpec660.core.statisticType.application

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Response
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticType
import java.util.*

class StatisticTypeResponse(
    val id: String,
    val keyCode: String,
    val name: String,
    val createdAt: Date,
    val updatedAt: Date?
) : Response {
    companion object {
        fun fromAggregate(statisticType: StatisticType): StatisticTypeResponse {
            return StatisticTypeResponse(
                statisticType.id().toString(),
                statisticType.keyCode(),
                statisticType.name(),
                statisticType.createdAt(),
                statisticType.updatedAt(),
            )
        }
    }
}
