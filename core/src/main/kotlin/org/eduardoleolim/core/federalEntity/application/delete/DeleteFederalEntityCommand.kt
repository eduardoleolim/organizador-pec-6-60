package org.eduardoleolim.core.federalEntity.application.delete

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class DeleteFederalEntityCommand(federalEntityId: String) : Command {
    private val federalEntityId: String = federalEntityId.trim()

    fun federalEntityId(): String {
        return federalEntityId
    }
}
