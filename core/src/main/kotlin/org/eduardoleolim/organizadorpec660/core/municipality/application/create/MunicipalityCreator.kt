package org.eduardoleolim.organizadorpec660.core.municipality.application.create

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.Municipality
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository

class MunicipalityCreator(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: FederalEntityRepository
) {
    fun create(keyCode: String, name: String, federalEntityId: String) {
        if (existsWithSameKeyCode(keyCode))
            throw MunicipalityAlreadyExistsError(keyCode)

        if (!existsFederalEntity(federalEntityId)) throw FederalEntityNotFoundError(federalEntityId)

        Municipality.create(keyCode, name, federalEntityId).let {
            municipalityRepository.save(it)
        }
    }

    private fun existsWithSameKeyCode(keyCode: String) = MunicipalityCriteria.keyCodeCriteria(keyCode).let {
        municipalityRepository.count(it) > 0
    }

    private fun existsFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
