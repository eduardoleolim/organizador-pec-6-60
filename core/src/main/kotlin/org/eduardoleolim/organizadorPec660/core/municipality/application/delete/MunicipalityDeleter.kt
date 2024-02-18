package org.eduardoleolim.organizadorPec660.core.municipality.application.delete

import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityCriteria
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityNotFoundError
import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityRepository

class MunicipalityDeleter(private val repository: MunicipalityRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw MunicipalityNotFoundError(id)

        repository.delete(id)
    }

    private fun exists(id: String) = MunicipalityCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
