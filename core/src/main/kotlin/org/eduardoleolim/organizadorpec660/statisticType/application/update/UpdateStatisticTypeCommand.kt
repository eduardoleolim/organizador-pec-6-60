package org.eduardoleolim.organizadorpec660.statisticType.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError

class UpdateStatisticTypeCommand(statisticTypeId: String, keyCode: String, name: String) :
    Command<StatisticTypeError, Unit> {
    private val statisticTypeId: String = statisticTypeId.trim()
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun statisticTypeId(): String {
        return statisticTypeId
    }

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}
