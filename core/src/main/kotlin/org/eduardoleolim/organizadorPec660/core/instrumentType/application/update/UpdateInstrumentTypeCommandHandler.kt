package org.eduardoleolim.organizadorPec660.core.instrumentType.application.update

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.command.CommandHandler

class UpdateInstrumentTypeCommandHandler(private val updater: InstrumentTypeUpdater) :
    CommandHandler<UpdateInstrumentTypeCommand> {
    override fun handle(command: UpdateInstrumentTypeCommand) {
        updater.update(command.id(), command.name())
    }
}