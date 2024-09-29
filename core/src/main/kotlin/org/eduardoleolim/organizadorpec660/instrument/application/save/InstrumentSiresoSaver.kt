package org.eduardoleolim.organizadorpec660.instrument.application.save

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentNotFoundError
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentRepository

class InstrumentSiresoSaver(private val instrumentRepository: InstrumentRepository) {
    fun saveInSIRESO(instrumentId: String): Either<InstrumentError, Unit> {
        val instrument = searchInstrument(instrumentId) ?: return Either.Left(InstrumentNotFoundError(instrumentId))

        if (instrument.savedInSIRESO().not()) {
            instrument.saveInSIRESO()
            instrumentRepository.save(instrument)
        }

        return Either.Right(Unit)
    }

    fun unsaveInSIRESO(instrumentId: String): Either<InstrumentError, Unit> {
        val instrument = searchInstrument(instrumentId) ?: return Either.Left(InstrumentNotFoundError(instrumentId))

        if (instrument.savedInSIRESO()) {
            instrument.unSaveInSIRESO()
            instrumentRepository.save(instrument)
        }

        return Either.Right(Unit)
    }

    private fun searchInstrument(instrumentId: String) = InstrumentCriteria.idCriteria(instrumentId).let {
        instrumentRepository.matching(it).firstOrNull()
    }
}
