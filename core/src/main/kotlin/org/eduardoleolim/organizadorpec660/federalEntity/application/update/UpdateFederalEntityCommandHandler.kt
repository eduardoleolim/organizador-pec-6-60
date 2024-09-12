package org.eduardoleolim.organizadorpec660.federalEntity.application.update

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateFederalEntityCommandHandler(private val updater: FederalEntityUpdater) :
    CommandHandler<FederalEntityError, Unit, UpdateFederalEntityCommand> {
    override fun handle(command: UpdateFederalEntityCommand): Either<FederalEntityError, Unit> {
        return updater.update(command.id(), command.keyCode(), command.name())
    }
}
