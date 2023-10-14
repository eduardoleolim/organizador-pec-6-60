package org.eduardoleolim.organizadorpec660.core.instrumentType.application.create

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class CreateInstrumentTypeCommandHandler(private val creator: InstrumentTypeCreator) :
    CommandHandler<CreateInstrumentTypeCommand> {
    override fun handle(command: CreateInstrumentTypeCommand) {
        creator.create(command.nombre())
    }
}
