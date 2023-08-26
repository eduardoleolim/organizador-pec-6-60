package org.eduardoleolim.core.federalEntity.application.delete

import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository

class FederalEntityDeleter(private val repository: FederalEntityRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw FederalEntityNotFoundError(id)

        FederalEntityId.fromString(id).let {
            repository.delete(it)
        }
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
