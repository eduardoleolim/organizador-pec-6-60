package org.eduardoleolim.core.federalEntity.application.update

import org.eduardoleolim.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.core.federalEntity.domain.FederalEntityRepository

class FederalEntityUpdater(private val repository: FederalEntityRepository) {
    fun update(id: String, keyCode: String, name: String) {
        val federalEntity = search(id) ?: throw FederalEntityNotFoundError(id)

        if (existsAnotherWithSameKeyCode(id, keyCode))
            throw FederalEntityAlreadyExistsError(keyCode)

        federalEntity.apply {
            changeKeyCode(keyCode)
            changeName(name)
        }.let {
            repository.save(it)
        }
    }

    private fun search(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.matching(it).firstOrNull()
    }

    private fun existsAnotherWithSameKeyCode(id: String, keyCode: String) =
        FederalEntityCriteria.anotherKeyCodeCriteria(id, keyCode).let {
            repository.count(it) > 0
        }
}
