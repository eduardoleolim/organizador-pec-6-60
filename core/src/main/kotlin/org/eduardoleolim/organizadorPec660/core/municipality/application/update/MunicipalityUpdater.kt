package org.eduardoleolim.organizadorPec660.core.municipality.application.update

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityAlreadyExistsError
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityRepository

class MunicipalityUpdater(
    private val municipalityRepository: MunicipalityRepository,
    private val federalEntityRepository: FederalEntityRepository
) {
    fun update(municipalityId: String, keyCode: String, name: String, federalEntityId: String) {
        val municipality = searchMunicipality(municipalityId) ?: throw MunicipalityNotFoundError(municipalityId)

        if (existsFederalEntity(federalEntityId).not())
            throw FederalEntityNotFoundError(federalEntityId)

        if (existsAnotherSameKeyCode(municipalityId, keyCode, federalEntityId))
            throw MunicipalityAlreadyExistsError(keyCode)

        municipality.apply {
            changeKeyCode(keyCode)
            changeName(name)
            changeFederalEntityId(federalEntityId)
        }.let {
            municipalityRepository.save(it)
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
