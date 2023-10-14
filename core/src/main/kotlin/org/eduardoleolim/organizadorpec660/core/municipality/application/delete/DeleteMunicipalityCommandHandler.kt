package org.eduardoleolim.organizadorpec660.core.municipality.application.delete

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class DeleteMunicipalityCommandHandler(private val deleter: MunicipalityDeleter) :
    CommandHandler<DeleteMunicipalityCommand> {
    override fun handle(command: DeleteMunicipalityCommand) {
        deleter.delete(command.municipalityId())
    }
}
