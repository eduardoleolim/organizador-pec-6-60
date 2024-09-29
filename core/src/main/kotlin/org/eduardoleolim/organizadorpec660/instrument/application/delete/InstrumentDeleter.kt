package org.eduardoleolim.organizadorpec660.instrument.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.instrument.domain.*

class InstrumentDeleter(private val repository: InstrumentRepository) {
    fun delete(instrumentId: String): Either<InstrumentError, Unit> {
        val instrument = search(instrumentId) ?: return Either.Left(InstrumentNotFoundError(instrumentId))

        if (instrument.savedInSIRESO()) {
            return Either.Left(CanNotDeleteSavedInstrumentError())
        }

        repository.delete(instrumentId)
        return Either.Right(Unit)
    }

    private fun search(instrumentId: String) = InstrumentCriteria.idCriteria(instrumentId).let {
        repository.matching(it).firstOrNull()
    }
}
