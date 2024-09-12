package org.eduardoleolim.organizadorpec660.instrument.application.delete

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.command.Command

class DeleteInstrumentCommand(instrumentId: String) : Command<InstrumentError, Unit> {
    private val instrumentId = instrumentId.trim()

    fun instrumentId(): String {
        return instrumentId
    }
}
