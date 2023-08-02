package org.eduardoleolim.core.federalEntity.application.delete

import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityId
import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFound
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository

class FederalEntityDeleter(private val repository: FederalEntityRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw FederalEntityNotFound(id)

        FederalEntityId.fromString(id).let {
            repository.delete(it)
        }
    }

    private fun exists(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.matching(it).isNotEmpty()
    }
}
