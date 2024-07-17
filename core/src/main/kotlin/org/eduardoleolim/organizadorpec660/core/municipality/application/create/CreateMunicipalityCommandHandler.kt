package org.eduardoleolim.organizadorpec660.core.municipality.application.create

import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import java.util.*

class CreateMunicipalityCommandHandler(private val creator: MunicipalityCreator) :
    CommandHandler<MunicipalityError, UUID, CreateMunicipalityCommand> {
    override fun handle(command: CreateMunicipalityCommand): Either<MunicipalityError, UUID> {
        return creator.create(command.keyCode(), command.name(), command.federalEntityId())
    }
}
