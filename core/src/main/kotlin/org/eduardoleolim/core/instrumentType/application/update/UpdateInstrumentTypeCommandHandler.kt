package org.eduardoleolim.core.instrumentType.application.update

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class UpdateInstrumentTypeCommandHandler(private val updater: InstrumentTypeUpdater) :
    CommandHandler<UpdateInstrumentTypeCommand> {
    override fun handle(command: UpdateInstrumentTypeCommand) {
        updater.update(command.id(), command.name())
    }
}
