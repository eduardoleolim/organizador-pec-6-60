package org.eduardoleolim.core.federalEntity.application.create

import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository

class FederalEntityCreator(private val repository: FederalEntityRepository) {
    fun create(keyCode: String, name: String) {
        if (exists(keyCode))
            throw FederalEntityAlreadyExistsError(keyCode)

        FederalEntity.create(keyCode, name).let {
            repository.save(it)
        }
    }

    private fun exists(keyCode: String) = FederalEntityCriteria.keyCodeCriteria(keyCode).let {
        repository.count(it) > 0
    }
}
