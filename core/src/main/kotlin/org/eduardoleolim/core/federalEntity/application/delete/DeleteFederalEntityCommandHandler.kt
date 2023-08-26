package org.eduardoleolim.core.federalEntity.application.delete

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class DeleteFederalEntityCommandHandler(private val deleter: FederalEntityDeleter) :
    CommandHandler<DeleteFederalEntityCommand> {
    override fun handle(command: DeleteFederalEntityCommand) {
        deleter.delete(command.federalEntityId())
    }
}
