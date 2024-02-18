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

        if (existsAnotherSameKeyCode(municipalityId, keyCode))
            throw MunicipalityAlreadyExistsError(keyCode)

        if (!existsFederalEntity(federalEntityId))
            throw FederalEntityNotFoundError(federalEntityId)

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

    private fun existsAnotherSameKeyCode(municipalityId: String, keyCode: String) =
        MunicipalityCriteria.anotherKeyCodeCriteria(municipalityId, keyCode).let {
            municipalityRepository.count(it) > 0
        }

    private fun existsFederalEntity(federalEntityId: String) = FederalEntityCriteria.idCriteria(federalEntityId).let {
        federalEntityRepository.count(it) > 0
    }
}
