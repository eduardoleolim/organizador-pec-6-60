package org.eduardoleolim.organizadorpec660.federalEntity.application.importer

import arrow.core.Either
import org.eduardoleolim.organizadorpec660.federalEntity.domain.*

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
                val federalEntity = searchFederalEntity(keyCode)

                when {
                    federalEntity != null && overrideIfExists -> {
                        federalEntity.apply {
                            changeName(name)
                            changeKeyCode(keyCode)
                        }.let {
                            repository.save(it)
                        }
                    }

                    federalEntity == null -> {
                        FederalEntity.create(keyCode, name).let {
                            repository.save(it)
                        }
                    }
                }
            } catch (e: Throwable) {
                warnings.add(FederalEntityImportWarning(index, e))
            }
        }

        if (warnings.size == federalEntities.size) {
            return Either.Left(CanNotImportFederalEntitiesError())
        }

        return Either.Right(warnings)
    }

    private fun searchFederalEntity(keyCode: String) = FederalEntityCriteria.keyCodeCriteria(keyCode).let {
        repository.matching(it).firstOrNull()
    }
}
