package org.eduardoleolim.organizadorpec660.core.agency.application.delete

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class DeleteAgencyCommand(agencyId: String) : Command {
    private val agencyId = agencyId.trim()

    fun agencyId(): String {
        return agencyId
    }
}
