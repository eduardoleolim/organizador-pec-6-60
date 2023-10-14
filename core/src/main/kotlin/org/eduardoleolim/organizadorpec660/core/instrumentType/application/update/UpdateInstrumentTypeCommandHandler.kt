package org.eduardoleolim.organizadorpec660.core.instrumentType.application.update

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateInstrumentTypeCommandHandler(private val updater: InstrumentTypeUpdater) :
    CommandHandler<UpdateInstrumentTypeCommand> {
    override fun handle(command: UpdateInstrumentTypeCommand) {
        updater.update(command.id(), command.name())
    }
}
