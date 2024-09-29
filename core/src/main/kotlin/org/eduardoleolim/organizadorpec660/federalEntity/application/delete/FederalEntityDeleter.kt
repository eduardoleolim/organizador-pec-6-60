package org.eduardoleolim.organizadorpec660.federalEntity.application.delete

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.*
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.municipality.domain.MunicipalityRepository

class FederalEntityDeleter(
    private val federalEntityRepository: FederalEntityRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun delete(id: String): Either<FederalEntityError, Unit> {
        try {
            if (exists(id).not())
                return Either.Left(FederalEntityNotFoundError(id))

            if (hasMunicipalities(id))
                return Either.Left(FederalEntityHasMunicipalitiesError())

            federalEntityRepository.delete(id)
            return Either.Right(Unit)
        } catch (e: InvalidArgumentFederalEntityException) {
            return Either.Left(CanNotDeleteFederalEntityError(e))
        }
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        federalEntityRepository.count(it) > 0
    }

    private fun hasMunicipalities(federalEntityId: String) =
        MunicipalityCriteria.federalEntityIdCriteria(federalEntityId).let {
            municipalityRepository.count(it) > 0
        }
}
