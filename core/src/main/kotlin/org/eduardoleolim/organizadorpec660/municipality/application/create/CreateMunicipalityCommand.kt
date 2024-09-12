package org.eduardoleolim.organizadorpec660.municipality.application.create

import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import java.util.*

class CreateMunicipalityCommand(keyCode: String, name: String, federalEntityId: String) :
    Command<MunicipalityError, UUID> {
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
