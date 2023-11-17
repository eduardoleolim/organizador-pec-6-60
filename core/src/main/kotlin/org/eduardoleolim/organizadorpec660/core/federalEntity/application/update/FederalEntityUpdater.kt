package org.eduardoleolim.organizadorpec660.core.federalEntity.application.update

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityCriteria
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityNotFoundError
import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.FederalEntityRepository

class FederalEntityUpdater(private val repository: FederalEntityRepository) {
    fun update(id: String, keyCode: String, name: String) {
        val federalEntity = searchFederalEntity(id) ?: throw FederalEntityNotFoundError(id)

        if (existsAnotherSameKeyCode(id, keyCode))
            throw FederalEntityAlreadyExistsError(keyCode)

        federalEntity.apply {
            changeKeyCode(keyCode)
            changeName(name)
        }.let {
            repository.save(it)
        }
    }

    private fun searchFederalEntity(id: String) = FederalEntityCriteria.idCriteria(id).let {
        repository.matching(it).firstOrNull()
    }

    private fun existsAnotherSameKeyCode(id: String, keyCode: String) =
        FederalEntityCriteria.anotherKeyCodeCriteria(id, keyCode).let {
            repository.count(it) > 0
        }
}
