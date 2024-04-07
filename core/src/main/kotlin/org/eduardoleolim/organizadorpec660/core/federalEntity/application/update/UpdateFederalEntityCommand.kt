package org.eduardoleolim.organizadorpec660.core.federalEntity.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class UpdateFederalEntityCommand(id: String, keyCode: String, name: String) : Command {
    private val id: String = id.trim()
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun id(): String {
        return id
    }

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}
