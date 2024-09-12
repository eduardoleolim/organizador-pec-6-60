package org.eduardoleolim.organizadorpec660.instrument.application.delete

import org.eduardoleolim.organizadorpec660.instrument.domain.*
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.Left
import org.eduardoleolim.organizadorpec660.shared.domain.Right

class InstrumentDeleter(private val repository: InstrumentRepository) {
    fun delete(instrumentId: String): Either<InstrumentError, Unit> {
        val instrument = search(instrumentId) ?: return Left(InstrumentNotFoundError(instrumentId))

        if (instrument.savedInSIRESO()) {
            return Left(CanNotDeleteSavedInstrumentError())
        }

        repository.delete(instrumentId)
        return Right(Unit)
    }

    private fun search(instrumentId: String) = InstrumentCriteria.idCriteria(instrumentId).let {
        repository.matching(it).firstOrNull()
    }
}
