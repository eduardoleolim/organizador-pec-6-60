package org.eduardoleolim.organizadorpec660.core.statisticType.application.delete

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.core.statisticType.domain.StatisticTypeError

class DeleteStatisticTypeCommand(statisticTypeId: String) : Command<StatisticTypeError, Unit> {
    private val statisticTypeId: String = statisticTypeId.trim()

    fun statisticTypeId(): String {
        return statisticTypeId
    }
}
