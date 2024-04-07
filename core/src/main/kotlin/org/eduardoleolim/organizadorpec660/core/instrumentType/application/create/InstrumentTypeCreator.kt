package org.eduardoleolim.organizadorpec660.core.instrumentType.application.create

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeAlreadyExistsError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository

class InstrumentTypeCreator(private val repository: InstrumentTypeRepository) {
    fun create(name: String) {
        if (existsAnotherSameName(name))
            throw InstrumentTypeAlreadyExistsError(name)

        InstrumentType.create(name).let {
            repository.save(it)
        }
    }

    private fun existsAnotherSameName(name: String) = InstrumentTypeCriteria.nameCriteria(name).let {
        repository.count(it) > 0
    }
}
