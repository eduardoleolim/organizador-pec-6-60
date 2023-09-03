package org.eduardoleolim.core.instrumentType.application.create

import org.eduardoleolim.shared.domain.bus.command.CommandHandler

class CreateInstrumentTypeCommandHandler(private val creator: InstrumentTypeCreator) :
    CommandHandler<CreateInstrumentTypeCommand> {
    override fun handle(command: CreateInstrumentTypeCommand) {
        creator.create(command.nombre())
    }
}
