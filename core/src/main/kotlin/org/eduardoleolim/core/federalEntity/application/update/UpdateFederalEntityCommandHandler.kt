package org.eduardoleolim.core.federalEntity.application.update

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class UpdateFederalEntityCommandHandler(private val updater: FederalEntityUpdater) :
    CommandHandler<UpdateFederalEntityCommand> {
    override fun handle(command: UpdateFederalEntityCommand) {
        updater.update(command.id(), command.keyCode(), command.name())
    }
}
