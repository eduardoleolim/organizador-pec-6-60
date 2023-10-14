package org.eduardoleolim.organizadorpec660.core.federalEntity.application.delete

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository

class FederalEntityDeleter(private val repository: FederalEntityRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw FederalEntityNotFoundError(id)

        repository.delete(FederalEntityId.fromString(id))
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
