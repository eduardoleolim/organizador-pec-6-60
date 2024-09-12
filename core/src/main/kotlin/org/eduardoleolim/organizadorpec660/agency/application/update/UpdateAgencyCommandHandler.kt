package org.eduardoleolim.organizadorpec660.agency.application.update

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateAgencyCommandHandler(private val updater: AgencyUpdater) :
    CommandHandler<AgencyError, Unit, UpdateAgencyCommand> {
    override fun handle(command: UpdateAgencyCommand): Either<AgencyError, Unit> {
        return updater.update(
            command.agencyId(),
            command.name(),
            command.consecutive(),
            command.municipalityId(),
            command.statisticTypeIds()
        )
    }
}
