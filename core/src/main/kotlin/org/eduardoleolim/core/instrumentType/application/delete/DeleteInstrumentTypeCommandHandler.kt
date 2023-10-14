package org.eduardoleolim.core.instrumentType.application.delete

import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class DeleteInstrumentTypeCommandHandler(private val deleter: InstrumentTypeDeleter) :
    CommandHandler<DeleteInstrumentTypeCommand> {
    override fun handle(command: DeleteInstrumentTypeCommand) {
        deleter.delete(command.id())
    }
}
