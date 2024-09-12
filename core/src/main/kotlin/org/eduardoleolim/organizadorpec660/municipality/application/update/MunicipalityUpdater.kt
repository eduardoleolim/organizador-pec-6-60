package org.eduardoleolim.organizadorpec660.municipality.application.update

import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.municipality.domain.*
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.Left
import org.eduardoleolim.organizadorpec660.shared.domain.Right

class MunicipalityUpdater(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: FederalEntityRepository
) {
    fun update(
        municipalityId: String,
        keyCode: String,
        name: String,
        federalEntityId: String
    ): Either<MunicipalityError, Unit> {
        try {
            val municipality =
                searchMunicipality(municipalityId) ?: return Left(MunicipalityNotFoundError(municipalityId))

            if (existsFederalEntity(federalEntityId).not())
                return Left(FederalEntityNotFoundError(federalEntityId))

            if (existsAnotherSameKeyCode(municipalityId, keyCode, federalEntityId))
                return Left(MunicipalityAlreadyExistsError(keyCode))

            municipality.apply {
                changeKeyCode(keyCode)
                changeName(name)
                changeFederalEntityId(federalEntityId)
            }.let {
                municipalityRepository.save(it)
                return Right(Unit)
            }
        } catch (e: InvalidArgumentMunicipalityException) {
            return Left(CanNotSaveMunicipalityError(e))
        }
    }

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.matching(it).firstOrNull()
    }

    private fun existsAnotherSameKeyCode(municipalityId: String, keyCode: String, federalEntityId: String) =
        MunicipalityCriteria.anotherKeyCodeCriteria(municipalityId, keyCode, federalEntityId).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
