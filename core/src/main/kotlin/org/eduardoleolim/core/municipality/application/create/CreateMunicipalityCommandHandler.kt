package org.eduardoleolim.core.municipality.application.create

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class CreateMunicipalityCommandHandler(private val creator: MunicipalityCreator) :
    CommandHandler<CreateMunicipalityCommand> {
    override fun handle(command: CreateMunicipalityCommand) {
        creator.create(command.keyCode(), command.name(), command.federalEntityId())
    }
}
