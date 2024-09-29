package org.eduardoleolim.organizadorpec660.agency.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class DeleteAgencyCommandHandler(private val agencyDeleter: AgencyDeleter) :
    CommandHandler<AgencyError, Unit, DeleteAgencyCommand> {
    override fun handle(command: DeleteAgencyCommand): Either<AgencyError, Unit> {
        return agencyDeleter.delete(command.agencyId())
    }
}
