package org.eduardoleolim.organizadorpec660.federalEntity.application.update

import org.eduardoleolim.organizadorpec660.federalEntity.domain.*
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.Left
import org.eduardoleolim.organizadorpec660.shared.domain.Right

class FederalEntityUpdater(private val repository: FederalEntityRepository) {
    fun update(id: String, keyCode: String, name: String): Either<FederalEntityError, Unit> {
        try {
            val federalEntity = searchFederalEntity(id) ?: return Left(FederalEntityNotFoundError(id))

            if (existsAnotherSameKeyCode(id, keyCode))
                return Left(FederalEntityAlreadyExistsError(keyCode))

            federalEntity.apply {
                changeKeyCode(keyCode)
                changeName(name)
            }.let {
                repository.save(it)
                return Right(Unit)
            }
        } catch (e: InvalidArgumentFederalEntityException) {
            return Left(CanNotSaveFederalEntityError(e))
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
