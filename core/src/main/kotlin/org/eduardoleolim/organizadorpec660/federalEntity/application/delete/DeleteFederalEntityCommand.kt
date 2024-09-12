package org.eduardoleolim.organizadorpec660.federalEntity.application.delete

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class DeleteFederalEntityCommand(federalEntityId: String) : Command<FederalEntityError, Unit> {
    private val federalEntityId: String = federalEntityId.trim()

    fun federalEntityId(): String {
        return federalEntityId
    }
}
