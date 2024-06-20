package org.eduardoleolim.organizadorpec660.core.agency.application.update

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class UpdateAgencyCommandHandler(private val updater: AgencyUpdater) : CommandHandler<UpdateAgencyCommand> {
    override fun handle(command: UpdateAgencyCommand) {
        updater.update(
            command.agencyId(),
            command.name(),
            command.consecutive(),
            command.municipalityId(),
            command.statisticTypeIds()
        )
    }
}
