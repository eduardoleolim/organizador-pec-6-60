package org.eduardoleolim.organizadorpec660.core.agency.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class CreateAgencyCommandHandler(private val creator: AgencyCreator) : CommandHandler<CreateAgencyCommand> {
    override fun handle(command: CreateAgencyCommand) {
        creator.create(command.name(), command.consecutive(), command.municipalities(), command.statisticTypes())
    }
}
