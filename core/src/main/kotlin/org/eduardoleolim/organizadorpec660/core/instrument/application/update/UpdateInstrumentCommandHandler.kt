package org.eduardoleolim.organizadorpec660.core.instrument.application.update

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler

class UpdateInstrumentCommandHandler(private val updater: InstrumentUpdater) :
    CommandHandler<InstrumentError, Unit, UpdateInstrumentCommand> {
    override fun handle(command: UpdateInstrumentCommand): Either<InstrumentError, Unit> {
        return updater.update(
            command.instrumentId(),
            command.statisticYear(),
            command.statisticMonth(),
            command.agencyId(),
            command.statisticTypeId(),
            command.municipalityId()
        )
    }
}
