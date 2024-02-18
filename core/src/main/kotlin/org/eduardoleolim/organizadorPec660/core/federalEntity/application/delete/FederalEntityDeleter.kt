package org.eduardoleolim.organizadorPec660.core.federalEntity.application.delete

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityRepository

class FederalEntityDeleter(private val repository: FederalEntityRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw FederalEntityNotFoundError(id)

        repository.delete(id)
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
