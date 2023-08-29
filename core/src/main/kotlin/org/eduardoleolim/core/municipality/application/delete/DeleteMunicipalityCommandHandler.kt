package org.eduardoleolim.core.municipality.application.delete

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class DeleteMunicipalityCommandHandler(private val deleter: MunicipalityDeleter) :
    CommandHandler<DeleteMunicipalityCommand> {
    override fun handle(command: DeleteMunicipalityCommand) {
        deleter.delete(command.municipalityId())
    }
}
