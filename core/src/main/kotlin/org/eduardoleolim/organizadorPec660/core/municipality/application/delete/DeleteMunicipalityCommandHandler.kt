package org.eduardoleolim.organizadorPec660.core.municipality.application.delete

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class DeleteMunicipalityCommandHandler(private val deleter: MunicipalityDeleter) :
    CommandHandler<DeleteMunicipalityCommand> {
    override fun handle(command: DeleteMunicipalityCommand) {
        deleter.delete(command.municipalityId())
    }
}
