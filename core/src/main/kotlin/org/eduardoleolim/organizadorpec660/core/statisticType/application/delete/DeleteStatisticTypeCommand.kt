package org.eduardoleolim.organizadorpec660.core.statisticType.application.delete

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class DeleteStatisticTypeCommand(statisticTypeId: String) : Command {
    private val statisticTypeId: String = statisticTypeId.trim()

    fun statisticTypeId(): String {
        return statisticTypeId
    }
}
