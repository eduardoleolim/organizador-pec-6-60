package org.eduardoleolim.organizadorPec660.core.federalEntity.application.create

import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorPec660.core.federalEntity.domain.FederalEntityRepository

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
