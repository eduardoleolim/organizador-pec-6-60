package org.eduardoleolim.organizadorpec660.core.agency.application.delete

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class DeleteAgencyCommandHandler(private val agencyDeleter: AgencyDeleter) : CommandHandler<DeleteAgencyCommand> {
    override fun handle(command: DeleteAgencyCommand) {
        agencyDeleter.delete(command.agencyId())
    }
}
