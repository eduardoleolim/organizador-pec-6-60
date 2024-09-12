package org.eduardoleolim.organizadorpec660.instrument.application.save

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentError
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentNotFoundError
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.Left
import org.eduardoleolim.organizadorpec660.shared.domain.Right

class InstrumentSiresoSaver(private val instrumentRepository: InstrumentRepository) {
    fun saveInSIRESO(instrumentId: String): Either<InstrumentError, Unit> {
        val instrument = searchInstrument(instrumentId) ?: return Left(InstrumentNotFoundError(instrumentId))

        if (instrument.savedInSIRESO().not()) {
            instrument.saveInSIRESO()
            instrumentRepository.save(instrument)
        }

        return Right(Unit)
    }

    fun unsaveInSIRESO(instrumentId: String): Either<InstrumentError, Unit> {
        val instrument = searchInstrument(instrumentId) ?: return Left(InstrumentNotFoundError(instrumentId))

        if (instrument.savedInSIRESO()) {
            instrument.unsaveInSIRESO()
            instrumentRepository.save(instrument)
        }

        return Right(Unit)
    }

    private fun searchInstrument(instrumentId: String) = InstrumentCriteria.idCriteria(instrumentId).let {
        instrumentRepository.matching(it).firstOrNull()
    }
}
