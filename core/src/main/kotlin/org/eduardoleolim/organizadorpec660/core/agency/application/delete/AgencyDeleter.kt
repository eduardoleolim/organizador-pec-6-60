package org.eduardoleolim.organizadorpec660.core.agency.application.delete

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyHasInstrumentsError
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyNotFoundError
import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentCriteria
import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository

class AgencyDeleter(
    private val agencyRepository: AgencyRepository,
    private val instrumentRepository: InstrumentRepository
) {
    fun delete(id: String) {
        if (exists(id).not())
            throw AgencyNotFoundError(id)

        if (hasInstruments(id))
            throw AgencyHasInstrumentsError()

        agencyRepository.delete(id)
    }

    private fun exists(id: String) = AgencyCriteria.idCriteria(id).let {
        agencyRepository.count(it) > 0
    }

    private fun hasInstruments(agencyId: String) = InstrumentCriteria.agencyCriteria(agencyId).let {
        instrumentRepository.count(it) > 0
    }
}
