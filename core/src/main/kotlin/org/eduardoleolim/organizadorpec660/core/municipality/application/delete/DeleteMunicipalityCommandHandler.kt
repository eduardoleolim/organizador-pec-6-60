package org.eduardoleolim.organizadorpec660.core.municipality.application.delete

import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class DeleteMunicipalityCommandHandler(private val deleter: MunicipalityDeleter) :
    CommandHandler<MunicipalityError, Unit, DeleteMunicipalityCommand> {
    override fun handle(command: DeleteMunicipalityCommand): Either<MunicipalityError, Unit> {
        return deleter.delete(command.municipalityId())
    }
}
