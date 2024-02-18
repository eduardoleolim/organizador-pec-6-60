package org.eduardoleolim.organizadorPec660.core.federalEntity.application.update

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class UpdateFederalEntityCommandHandler(private val updater: FederalEntityUpdater) :
    CommandHandler<UpdateFederalEntityCommand> {
    override fun handle(command: UpdateFederalEntityCommand) {
        updater.update(command.id(), command.keyCode(), command.name())
    }
}
