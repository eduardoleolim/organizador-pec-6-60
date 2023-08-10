package org.eduardoleolim.core.federalEntity.application.create

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class CreateFederalEntityCommandHandler(private val creator: FederalEntityCreator) :
    CommandHandler<CreateFederalEntityCommand> {
    override fun handle(command: CreateFederalEntityCommand) {
        creator.create(command.keyCode(), command.name())
    }
}
