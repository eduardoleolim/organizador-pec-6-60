package org.eduardoleolim.core.federalEntity.application.create

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class CreateFederalEntityCommandHandler(private val creator: FederalEntityCreator) :
    CommandHandler<CreateFederalEntityCommand> {
    override fun handle(command: CreateFederalEntityCommand) {
        creator.create(command.keyCode(), command.name())
    }
}
