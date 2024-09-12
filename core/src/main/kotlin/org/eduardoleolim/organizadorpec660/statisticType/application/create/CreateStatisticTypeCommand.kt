package org.eduardoleolim.organizadorpec660.statisticType.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import org.eduardoleolim.organizadorpec660.statisticType.domain.StatisticTypeError
import java.util.*

class CreateStatisticTypeCommand(keyCode: String, name: String) : Command<StatisticTypeError, UUID> {
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}
