package org.eduardoleolim.core.federalEntity.application.update

import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFound
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository

class FederalEntityUpdater(private val repository: FederalEntityRepository) {
    fun update(id: String, keyCode: String, name: String) {
        val federalEntity = search(id)

        federalEntity.apply {
            changeKeyCode(keyCode)
            changeName(name)
        }.let {
            repository.save(it)
        }
    }

    private fun search(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.matching(it).firstOrNull() ?: throw FederalEntityNotFound(id)
    }
}
