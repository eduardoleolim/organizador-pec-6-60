package org.eduardoleolim.core.federalEntity.application.update

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateFederalEntityCommandHandler(private val updater: FederalEntityUpdater) :
    CommandHandler<UpdateFederalEntityCommand> {
    override fun handle(command: UpdateFederalEntityCommand) {
        updater.update(command.id(), command.keyCode(), command.name())
    }
}
