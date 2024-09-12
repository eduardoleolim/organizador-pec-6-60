package org.eduardoleolim.organizadorpec660.agency.application.delete

import org.eduardoleolim.organizadorpec660.agency.domain.*
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right

class AgencyDeleter(
    private val agencyRepository: AgencyRepository,
    private val instrumentRepository: InstrumentRepository
) {
    fun delete(id: String): Either<AgencyError, Unit> {
        try {
            if (exists(id).not())
                return Left(AgencyNotFoundError(id))

            if (hasInstruments(id))
                return Left(AgencyHasInstrumentsError())

            agencyRepository.delete(id)
            return Right(Unit)
        } catch (e: InvalidArgumentAgencyException) {
            return Left(CanNotDeleteAgencyError(e))
        }
    }

    private fun exists(id: String) = AgencyCriteria.idCriteria(id).let {
        agencyRepository.count(it) > 0
    }

    private fun hasInstruments(agencyId: String) = InstrumentCriteria.agencyCriteria(agencyId).let {
        instrumentRepository.count(it) > 0
    }
}
