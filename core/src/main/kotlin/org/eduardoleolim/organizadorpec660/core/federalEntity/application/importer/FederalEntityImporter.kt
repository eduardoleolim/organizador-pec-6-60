package org.eduardoleolim.organizadorpec660.core.federalEntity.application.importer

import org.eduardoleolim.organizadorpec660.core.federalEntity.domain.*
import org.eduardoleolim.organizadorpec660.core.shared.domain.Either
import org.eduardoleolim.organizadorpec660.core.shared.domain.Left
import org.eduardoleolim.organizadorpec660.core.shared.domain.Right

data class FederalEntityImportWarning(val index: Int, val error: Throwable)

class FederalEntityImporter<I : FederalEntityImportInput>(
    private val repository: FederalEntityRepository,
    private val reader: FederalEntityImportReader<I>
) {
    fun import(
        input: I,
        overrideIfExists: Boolean
    ): Either<FederalEntityError, List<FederalEntityImportWarning>> {
        val warnings = mutableListOf<FederalEntityImportWarning>()
        val federalEntities = reader.read(input)

        federalEntities.forEachIndexed { index, federalEntityData ->
            try {
                val keyCode = federalEntityData.keyCode()
                val name = federalEntityData.name()
                val federalEntity = searchFederalEntityByKeyCode(keyCode)

                if (federalEntity != null && overrideIfExists) {
                    federalEntity.apply {
                        changeName(name)
                        changeKeyCode(keyCode)
                    }
                    repository.save(federalEntity)
                } else {
                    FederalEntity.create(keyCode, name).let {
                        repository.save(it)
                    }
                }
            } catch (e: Throwable) {
                warnings.add(FederalEntityImportWarning(index, e))
            }
        }

        if (warnings.size == federalEntities.size) {
            return Left(CanNotImportFederalEntitiesError())
        }

        return Right(warnings)
    }

    private fun searchFederalEntityByKeyCode(keyCode: String) = FederalEntityCriteria.keyCodeCriteria(keyCode).let {
        repository.matching(it).firstOrNull()
    }
}
