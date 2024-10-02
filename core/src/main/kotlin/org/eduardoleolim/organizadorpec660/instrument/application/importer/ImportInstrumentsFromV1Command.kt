package org.eduardoleolim.organizadorpec660.instrument.application.importer

import org.eduardoleolim.organizadorpec660.instrument.domain.AccdbInstrumentImportInput
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.Command

class ImportInstrumentsFromV1Command<I : AccdbInstrumentImportInput>(
    private val input: I,
    private val overrideIfExists: Boolean
) : Command<InstrumentError, List<InstrumentImportWarning>> {
    fun input(): I {
        return input
    }

    fun overrideIfExists(): Boolean {
        return overrideIfExists
    }
}
