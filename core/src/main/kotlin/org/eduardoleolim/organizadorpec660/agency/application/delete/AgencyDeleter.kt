package org.eduardoleolim.organizadorpec660.agency.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.*
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentRepository

class AgencyDeleter(
    private val agencyRepository: AgencyRepository,
    private val instrumentRepository: InstrumentRepository
) {
    fun delete(id: String): Either<AgencyError, Unit> {
        try {
            if (exists(id).not())
                return Either.Left(AgencyNotFoundError(id))

            if (hasInstruments(id))
                return Either.Left(AgencyHasInstrumentsError())

            agencyRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentAgencyException) {
            return Either.Left(CanNotDeleteAgencyError(e))
        }
    }

    private fun exists(id: String) = AgencyCriteria.idCriteria(id).let {
        agencyRepository.count(it) > 0
    }

    private fun hasInstruments(agencyId: String) = InstrumentCriteria.agencyCriteria(agencyId).let {
        instrumentRepository.count(it) > 0
    }
}
