package org.eduardoleolim.core.municipality.application.create

import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFound
import org.eduardoleolim.core.federalEntity.infrastructure.persistence.KtormFederalEntityRepository
import org.eduardoleolim.core.municipality.domain.Municipality
import org.eduardoleolim.core.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.core.municipality.domain.MunicipalityRepository

class MunicipalityCreator(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: KtormFederalEntityRepository
) {
    fun create(keyCode: String, name: String, federalEntityId: String) {
        if (existsWithSameKeyCode(keyCode))
            throw MunicipalityAlreadyExistsError(keyCode)

        val federalEntity = searchFederalEntity(federalEntityId) ?: throw FederalEntityNotFound(federalEntityId)

        Municipality.create(keyCode, name, federalEntity).let {
            municipalityRepository.save(it)
        }
    }

    private fun existsWithSameKeyCode(keyCode: String) = MunicipalityCriteria.keyCodeCriteria(keyCode).let {
        federalEntityRepository.count(it) > 0
    }

    private fun searchFederalEntity(federalEntityId: String) = MunicipalityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.matching(it).firstOrNull()
    }
}
