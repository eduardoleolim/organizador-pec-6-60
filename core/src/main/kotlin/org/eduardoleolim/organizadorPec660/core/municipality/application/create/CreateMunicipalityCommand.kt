package org.eduardoleolim.organizadorPec660.core.municipality.application.create

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command

class CreateMunicipalityCommand(keyCode: String, name: String, federalEntityId: String) : Command {
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()
    private val federalEntityId: String = federalEntityId.trim()

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }

    fun federalEntityId(): String {
        return federalEntityId
    }
}
