package org.eduardoleolim.organizadorpec660.instrument.application.create

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.CommandHandler
import java.util.*

class CreateInstrumentCommandHandler(private val creator: InstrumentCreator) :
    CommandHandler<InstrumentError, UUID, CreateInstrumentCommand> {
    override fun handle(command: CreateInstrumentCommand): Either<InstrumentError, UUID> {
        return creator.create(
            command.statisticYear(),
            command.statisticMonth(),
            command.agencyId(),
            command.statisticTypeId(),
            command.municipalityId(),
            command.file()
        )
    }
}
