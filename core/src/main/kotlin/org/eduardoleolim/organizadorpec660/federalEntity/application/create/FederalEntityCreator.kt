package org.eduardoleolim.organizadorpec660.federalEntity.application.create

import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right
import org.eduardoleolim.organizadorpec660.federalEntity.domain.*
import java.util.*

class FederalEntityCreator(private val repository: FederalEntityRepository) {
    fun create(keyCode: String, name: String): Either<FederalEntityError, UUID> {
        try {
            if (exists(keyCode))
                return Left(FederalEntityAlreadyExistsError(keyCode))

            FederalEntity.create(keyCode, name).let {
                repository.save(it)
                return Right(it.id())
            }
        } catch (e: InvalidArgumentFederalEntityException) {
            return Left(CanNotSaveFederalEntityError(e))
        }
    }

    private fun exists(keyCode: String) = FederalEntityCriteria.keyCodeCriteria(keyCode).let {
        repository.count(it) > 0
    }
}
