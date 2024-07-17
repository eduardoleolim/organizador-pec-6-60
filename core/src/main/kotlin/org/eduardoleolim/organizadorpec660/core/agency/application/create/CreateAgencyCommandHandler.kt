package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
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
