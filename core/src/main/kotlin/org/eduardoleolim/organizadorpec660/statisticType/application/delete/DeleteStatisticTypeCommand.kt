package org.eduardoleolim.organizadorpec660.statisticType.application.delete

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError

class DeleteStatisticTypeCommand(statisticTypeId: String) : Command<StatisticTypeError, Unit> {
    private val statisticTypeId: String = statisticTypeId.trim()

    fun statisticTypeId(): String {
        return statisticTypeId
    }
}
