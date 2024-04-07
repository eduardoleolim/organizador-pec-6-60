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
        if (existsFederalEntity(federalEntityId).not())
            throw FederalEntityNotFoundError(federalEntityId)

        if (existsMunicipality(keyCode, federalEntityId))
            throw MunicipalityAlreadyExistsError(keyCode)

        Municipality.create(keyCode, name, federalEntityId).let {
            municipalityRepository.save(it)
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
