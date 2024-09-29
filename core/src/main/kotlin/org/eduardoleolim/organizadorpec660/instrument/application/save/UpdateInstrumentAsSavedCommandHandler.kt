package org.eduardoleolim.organizadorpec660.instrument.application.save

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class UpdateInstrumentAsSavedCommandHandler(private val instrumentSiresoSaver: InstrumentSiresoSaver) :
    CommandHandler<InstrumentError, Unit, UpdateInstrumentAsSavedCommand> {
    override fun handle(command: UpdateInstrumentAsSavedCommand): Either<InstrumentError, Unit> {
        return instrumentSiresoSaver.saveInSIRESO(command.instrumentId())
    }
}
