package org.eduardoleolim.organizadorpec660.federalEntity.application.delete

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class DeleteFederalEntityCommandHandler(private val deleter: FederalEntityDeleter) :
    CommandHandler<FederalEntityError, Unit, DeleteFederalEntityCommand> {
    override fun handle(command: DeleteFederalEntityCommand): Either<FederalEntityError, Unit> {
        return deleter.delete(command.federalEntityId())
    }
}
