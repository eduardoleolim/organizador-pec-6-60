package org.eduardoleolim.organizadorpec660.core.statisticType.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class CreateStatisticTypeCommand(keyCode: String, name: String) : Command {
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}
