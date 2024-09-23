package org.eduardoleolim.organizadorpec660.municipality.application.delete

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyCriteria
import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.municipality.domain.*
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.Left
import org.eduardoleolim.organizadorpec660.shared.domain.Right

class MunicipalityDeleter(
    private val municipalityRepository: MunicipalityRepository,
    private val agencyRepository: AgencyRepository
) {
    fun delete(id: String): Either<MunicipalityError, Unit> {
        try {
            if (!exists(id))
                return Left(MunicipalityNotFoundError(id))

            if (hasAgencies(id))
                return Left(MunicipalityHasAgenciesError())

            municipalityRepository.delete(id)
            return Right(Unit)
        } catch (e: InvalidArgumentMunicipalityException) {
            return Left(CanNotDeleteMunicipalityError(e))
        }
    }

    private fun exists(id: String) = MunicipalityCriteria.idCriteria(id).let {
        municipalityRepository.count(it) > 0
    }

    private fun hasAgencies(municipalityId: String) = AgencyCriteria.municipalityIdCriteria(municipalityId).let {
        agencyRepository.count(it) > 0
    }
}
