package org.eduardoleolim.organizadorpec660.instrument.application.save

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateInstrumentAsNotSavedCommandHandler(private val instrumentSiresoSaver: InstrumentSiresoSaver) :
    CommandHandler<InstrumentError, Unit, UpdateInstrumentAsNotSavedCommand> {
    override fun handle(command: UpdateInstrumentAsNotSavedCommand): Either<InstrumentError, Unit> {
        return instrumentSiresoSaver.unsaveInSIRESO(command.instrumentId())
    }
}
