package org.eduardoleolim.organizadorpec660.core.municipality.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class UpdateMunicipalityCommand(municipalityId: String, name: String, keyCode: String, federalEntityId: String) :
    Command {
    private val municipalityId: String = municipalityId.trim()
    private val name: String = name.trim().uppercase()
    private val keyCode: String = keyCode.trim().uppercase()
    private val federalEntityId: String = federalEntityId.trim()

    fun municipalityId(): String {
        return municipalityId
    }

    fun name(): String {
        return name
    }

    fun keyCode(): String {
        return keyCode
    }

    fun federalEntityId(): String {
        return federalEntityId
    }
}
