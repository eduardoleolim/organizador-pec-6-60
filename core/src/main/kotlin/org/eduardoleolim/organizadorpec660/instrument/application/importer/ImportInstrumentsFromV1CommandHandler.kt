package org.eduardoleolim.organizadorpec660.instrument.application.importer

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.instrument.domain.AccdbInstrumentImportInput
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.shared.domain.bus.command.CommandHandler

class ImportInstrumentsFromV1CommandHandler<I : AccdbInstrumentImportInput>(private val importer: InstrumentFromV1Importer<I>) :
    CommandHandler<InstrumentError, List<InstrumentImportWarning>, ImportInstrumentsFromV1Command<I>> {
    override fun handle(command: ImportInstrumentsFromV1Command<I>): Either<InstrumentError, List<InstrumentImportWarning>> {
        return importer.import(command.input(), command.overrideIfExists())
    }
}
