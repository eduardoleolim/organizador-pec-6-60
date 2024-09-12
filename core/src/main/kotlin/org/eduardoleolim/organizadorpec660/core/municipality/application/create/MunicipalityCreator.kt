package org.eduardoleolim.organizadorpec660.core.municipality.application.create

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.*
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import java.util.*

class MunicipalityCreator(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: FederalEntityRepository
) {
    fun create(keyCode: String, name: String, federalEntityId: String): Either<MunicipalityError, UUID> {
        try {
            if (existsFederalEntity(federalEntityId).not())
                return Left(FederalEntityNotFoundError(federalEntityId))

            if (existsMunicipality(keyCode, federalEntityId))
                return Left(MunicipalityAlreadyExistsError(keyCode))

            Municipality.create(keyCode, name, federalEntityId).let {
                municipalityRepository.save(it)
                return Right(it.id())
            }
        } catch (e: InvalidArgumentMunicipalityException) {
            return Left(CanNotSaveMunicipalityError(e))
        }
    }

    private fun existsMunicipality(keyCode: String, federalEntityId: String) =
        MunicipalityCriteria.keyCodeAndFederalEntityIdCriteria(keyCode, federalEntityId).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
