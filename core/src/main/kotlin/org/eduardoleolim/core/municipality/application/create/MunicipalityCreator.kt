package org.eduardoleolim.core.municipality.application.create

import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.core.municipality.domain.Municipality
import org.eduardoleolim.core.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.core.municipality.domain.MunicipalityRepository

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
        federalEntityRepository.count(it) > 0
    }

    private fun existsFederalEntity(federalEntityId: String) = MunicipalityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
