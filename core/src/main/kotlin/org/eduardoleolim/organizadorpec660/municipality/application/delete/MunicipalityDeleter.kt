package org.eduardoleolim.organizadorpec660.municipality.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.municipality.domain.*

class MunicipalityDeleter(
    private val municipalityRepository: MunicipalityRepository,
    private val agencyRepository: AgencyRepository
) {
    fun delete(id: String): Either<MunicipalityError, Unit> {
        try {
            if (!exists(id))
                return Either.Left(MunicipalityNotFoundError(id))

            if (hasAgencies(id))
                return Either.Left(MunicipalityHasAgenciesError())

            municipalityRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentMunicipalityException) {
            return Either.Left(CanNotDeleteMunicipalityError(e))
        }
    }

    private fun exists(id: String) = MunicipalityCriteria.idCriteria(id).let {
        municipalityRepository.count(it) > 0
    }

    private fun hasAgencies(municipalityId: String) = AgencyCriteria.municipalityIdCriteria(municipalityId).let {
        agencyRepository.count(it) > 0
    }
}
