package org.eduardoleolim.organizadorpec660.core.federalEntity.application.create

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command
import java.util.*

class CreateFederalEntityCommand(keyCode: String, name: String) : Command<FederalEntityError, UUID> {
    private val keyCode: String = keyCode.trim().uppercase()
    private val name: String = name.trim().uppercase()

    fun keyCode(): String {
        return keyCode
    }

    fun name(): String {
        return name
    }
}
