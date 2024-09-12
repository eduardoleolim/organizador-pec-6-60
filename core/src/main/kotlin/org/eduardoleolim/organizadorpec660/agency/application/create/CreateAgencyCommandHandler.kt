package org.eduardoleolim.organizadorpec660.agency.application.create

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler
import java.util.*

class CreateAgencyCommandHandler(private val creator: AgencyCreator) :
    CommandHandler<AgencyError, UUID, CreateAgencyCommand> {
    override fun handle(command: CreateAgencyCommand): Either<AgencyError, UUID> {
        return creator.create(
            command.name(),
            command.consecutive(),
            command.municipalityId(),
            command.statisticTypeIds()
        )
    }
}
