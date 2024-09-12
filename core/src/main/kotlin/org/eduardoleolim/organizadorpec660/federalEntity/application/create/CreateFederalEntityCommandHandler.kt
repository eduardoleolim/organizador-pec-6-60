package org.eduardoleolim.organizadorpec660.federalEntity.application.create

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityError
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import java.util.*

class CreateFederalEntityCommandHandler(private val creator: FederalEntityCreator) :
    CommandHandler<FederalEntityError, UUID, CreateFederalEntityCommand> {
    override fun handle(command: CreateFederalEntityCommand): Either<FederalEntityError, UUID> {
        return creator.create(command.keyCode(), command.name())
    }
}
