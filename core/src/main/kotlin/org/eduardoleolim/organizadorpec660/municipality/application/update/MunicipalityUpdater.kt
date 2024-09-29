package org.eduardoleolim.organizadorpec660.municipality.application.update

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.municipality.domain.*

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
                searchMunicipality(municipalityId) ?: return Either.Left(MunicipalityNotFoundError(municipalityId))

            if (existsFederalEntity(federalEntityId).not())
                return Either.Left(FederalEntityNotFoundError(federalEntityId))

            if (existsAnotherSameKeyCode(municipalityId, keyCode, federalEntityId))
                return Either.Left(MunicipalityAlreadyExistsError(keyCode))

            municipality.apply {
                changeKeyCode(keyCode)
                changeName(name)
                changeFederalEntityId(federalEntityId)
            }.let {
                municipalityRepository.save(it)
                return Either.Right(Unit)
            }
        } catch (e: InvalidArgumentMunicipalityException) {
            return Either.Left(CanNotSaveMunicipalityError(e))
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
