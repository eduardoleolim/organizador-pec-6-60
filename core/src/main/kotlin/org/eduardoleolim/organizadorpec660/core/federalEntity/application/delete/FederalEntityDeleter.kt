package org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.*
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right

class FederalEntityDeleter(
    private val federalEntityRepository: FederalEntityRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun delete(id: String): Either<FederalEntityError, Unit> {
        try {
            if (exists(id).not())
                return Left(FederalEntityNotFoundError(id))

            if (hasMunicipalities(id))
                return Left(FederalEntityHasMunicipalitiesError())

            federalEntityRepository.delete(id)
            return Right(Unit)
        } catch (e: InvalidArgumentFederalEntityException) {
            return Left(CanNotDeleteFederalEntityError(e))
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
