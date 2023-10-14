package org.eduardoleolim.organizadorpec660.core.federalEntity.application.create

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class CreateFederalEntityCommand(keyCode: String, name: String) : Command {
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}
