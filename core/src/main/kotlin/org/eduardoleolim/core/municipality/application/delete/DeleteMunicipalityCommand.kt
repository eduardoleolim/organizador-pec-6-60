package org.eduardoleolim.core.municipality.application.delete

import org.eduardoleolim.shared.domain.bus.command.Command

class DeleteMunicipalityCommand(municipalityId: String) : Command {
    private val municipalityId: String = municipalityId.trim()

    fun municipalityId(): String {
        return municipalityId
    }
}