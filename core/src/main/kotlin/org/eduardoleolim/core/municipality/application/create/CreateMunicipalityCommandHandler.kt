package org.eduardoleolim.core.municipality.application.create

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class CreateMunicipalityCommandHandler(private val creator: MunicipalityCreator) :
    CommandHandler<CreateMunicipalityCommand> {
    override fun handle(command: CreateMunicipalityCommand) {
        creator.create(command.keyCode(), command.name(), command.federalEntityId())
    }
}
