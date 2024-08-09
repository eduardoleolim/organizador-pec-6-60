package org.eduardoleolim.organizadorpec660.core.instrument.application.save

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class UpdateInstrumentAsSavedCommandHandler(private val instrumentSiresoSaver: InstrumentSiresoSaver) :
    CommandHandler<InstrumentError, Unit, UpdateInstrumentAsSavedCommand> {
    override fun handle(command: UpdateInstrumentAsSavedCommand): Either<InstrumentError, Unit> {
        return instrumentSiresoSaver.saveInSIRESO(command.instrumentId())
    }
}
