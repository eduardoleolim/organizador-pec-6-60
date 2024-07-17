package org.eduardoleolim.organizadorpec660.core.agency.application.delete

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class DeleteAgencyCommandHandler(private val agencyDeleter: AgencyDeleter) :
    CommandHandler<AgencyError, Unit, DeleteAgencyCommand> {
    override fun handle(command: DeleteAgencyCommand): Either<AgencyError, Unit> {
        return agencyDeleter.delete(command.agencyId())
    }
}
