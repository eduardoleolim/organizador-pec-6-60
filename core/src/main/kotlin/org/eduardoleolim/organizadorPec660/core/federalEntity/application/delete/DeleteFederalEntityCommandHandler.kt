package org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class DeleteFederalEntityCommandHandler(private val deleter: FederalEntityDeleter) :
    CommandHandler<DeleteFederalEntityCommand> {
    override fun handle(command: DeleteFederalEntityCommand) {
        deleter.delete(command.federalEntityId())
    }
}
