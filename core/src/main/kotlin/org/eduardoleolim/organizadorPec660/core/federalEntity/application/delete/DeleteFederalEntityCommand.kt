package org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.Command

class DeleteFederalEntityCommand(federalEntityId: String) : Command {
    private val federalEntityId: String = federalEntityId.trim()

    fun federalEntityId(): String {
        return federalEntityId
    }
}
