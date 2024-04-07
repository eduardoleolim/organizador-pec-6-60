package org.eduardoleolim.organizadorpec660.core.municipality.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class UpdateMunicipalityCommandHandler(private val updater: MunicipalityUpdater) :
    CommandHandler<UpdateMunicipalityCommand> {
    override fun handle(command: UpdateMunicipalityCommand) {
        updater.update(command.municipalityId(), command.keyCode(), command.name(), command.federalEntityId())
    }
}
