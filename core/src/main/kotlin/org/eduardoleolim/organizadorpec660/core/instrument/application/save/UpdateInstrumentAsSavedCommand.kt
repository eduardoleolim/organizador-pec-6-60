package org.eduardoleolim.organizadorpec660.core.instrument.application.save

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class UpdateInstrumentAsSavedCommand(instrumentId: String) : Command<InstrumentError, Unit> {
    private val instrumentId: String = instrumentId.trim()

    fun instrumentId(): String {
        return instrumentId
    }
}
