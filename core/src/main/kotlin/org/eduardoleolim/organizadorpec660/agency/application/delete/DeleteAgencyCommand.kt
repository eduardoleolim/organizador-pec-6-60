package org.eduardoleolim.organizadorpec660.agency.application.delete

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class DeleteAgencyCommand(agencyId: String) : Command<AgencyError, Unit> {
    private val agencyId = agencyId.trim()

    fun agencyId(): String {
        return agencyId
    }
}
