package org.eduardoleolim.core.municipality.application.update

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateMunicipalityCommandHandler(private val updater: MunicipalityUpdater) :
    CommandHandler<UpdateMunicipalityCommand> {
    override fun handle(command: UpdateMunicipalityCommand) {
        updater.update(command.municipalityId(), command.name(), command.keyCode(), command.federalEntityId())
    }
}
