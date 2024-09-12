package org.eduardoleolim.organizadorpec660.municipality.application.delete

import org.eduardoleolim.organizadorpec660.municipality.domain.*
import org.eduardoleolim.organizadorpec660.shared.domain.Either
import org.eduardoleolim.organizadorpec660.shared.domain.Left
import org.eduardoleolim.organizadorpec660.shared.domain.Right

class MunicipalityDeleter(private val repository: MunicipalityRepository) {
    fun delete(id: String): Either<MunicipalityError, Unit> {
        try {
            if (!exists(id))
                return Left(MunicipalityNotFoundError(id))

            repository.delete(id)
            return Right(Unit)
        } catch (e: InvalidArgumentMunicipalityException) {
            return Left(CanNotDeleteMunicipalityError(e))
        }
    }

    private fun exists(id: String) = MunicipalityCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
