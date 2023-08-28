package org.eduardoleolim.core.municipality.application.update

import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.core.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.core.municipality.domain.MunicipalityRepository

class MunicipalityUpdater(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: FederalEntityRepository
) {
    fun update(municipalityId: String, keyCode: String, name: String, federalEntityId: String) {
        val municipality = searchMunicipality(municipalityId) ?: throw MunicipalityNotFoundError(municipalityId)

        if (existsAnotherSameKeyCode(municipalityId, keyCode))
            throw MunicipalityAlreadyExistsError(keyCode)

        if (!existsFederalEntity(federalEntityId))
            throw FederalEntityNotFoundError(federalEntityId)

        municipality.apply {
            changeKeyCode(keyCode)
            changeName(name)
            changeFederalEntityId(federalEntityId)
            municipalityRepository.save(this)
        }
    }

    private fun searchMunicipality(municipalityId: String) = MunicipalityCriteria.idCriteria(municipalityId).let {
        municipalityRepository.matching(it).firstOrNull()
    }

    private fun existsAnotherSameKeyCode(municipalityId: String, keyCode: String) =
        MunicipalityCriteria.anotherKeyCodeCriteria(municipalityId, keyCode).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
