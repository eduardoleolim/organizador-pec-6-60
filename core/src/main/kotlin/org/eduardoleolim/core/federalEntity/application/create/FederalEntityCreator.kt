package org.eduardoleolim.core.federalEntity.application.create

import org.eduardoleolim.core.federalEntity.domain.FederalEntity
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository

class FederalEntityCreator(private val repository: FederalEntityRepository) {
    fun create(keyCode: String, name: String) {
        FederalEntity.create(keyCode, name).let {
            repository.save(it)
        }
    }
}
