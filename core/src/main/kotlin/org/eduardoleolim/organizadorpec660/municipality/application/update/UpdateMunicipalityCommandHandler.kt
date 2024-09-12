package org.eduardoleolim.organizadorpec660.municipality.application.update

import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateMunicipalityCommandHandler(private val updater: MunicipalityUpdater) :
    CommandHandler<MunicipalityError, Unit, UpdateMunicipalityCommand> {
    override fun handle(command: UpdateMunicipalityCommand): Either<MunicipalityError, Unit> {
        return updater.update(command.municipalityId(), command.keyCode(), command.name(), command.federalEntityId())
    }
}
