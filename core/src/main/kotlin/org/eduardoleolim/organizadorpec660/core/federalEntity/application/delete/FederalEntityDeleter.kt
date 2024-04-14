package org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityHasMunicipalitiesError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorpec660.core.municipality.domain.MunicipalityRepository

class FederalEntityDeleter(
    private val federalEntityRepository: FederalEntityRepository,
    private val municipalityRepository: MunicipalityRepository
) {
    fun delete(id: String) {
        if (exists(id).not())
            throw FederalEntityNotFoundError(id)

        if (hasMunicipalities(id))
            throw FederalEntityHasMunicipalitiesError()

        federalEntityRepository.delete(id)
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        federalEntityRepository.count(it) > 0
    }

    private fun hasMunicipalities(federalEntityId: String) =
        MunicipalityCriteria.federalEntityIdCriteria(federalEntityId).let {
            municipalityRepository.count(it) > 0
        }
}
