package org.eduardoleolim.organizadorpec660.core.municipality.application.update

import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class UpdateMunicipalityCommand(municipalityId: String, keyCode: String, name: String, federalEntityId: String) :
    Command<MunicipalityError, Unit> {
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
