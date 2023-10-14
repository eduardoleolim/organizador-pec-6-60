package org.eduardoleolim.organizadorpec660.core.municipality.application.delete

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class DeleteMunicipalityCommand(municipalityId: String) : Command {
    private val municipalityId: String = municipalityId.trim()

    fun municipalityId(): String {
        return municipalityId
    }
}
