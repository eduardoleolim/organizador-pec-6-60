package org.eduardoleolim.organizadorpec660.instrument.application.delete

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class DeleteInstrumentCommandHandler(private val deleter: InstrumentDeleter) :
    CommandHandler<InstrumentError, Unit, DeleteInstrumentCommand> {
    override fun handle(command: DeleteInstrumentCommand): Either<InstrumentError, Unit> {
        return deleter.delete(command.instrumentId())
    }
}
