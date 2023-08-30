package org.eduardoleolim.core.instrumentType.application.create

import org.eduardoleolim.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeAlreadyExistsError
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeRepository

class InstrumentTypeCreator(private val repository: InstrumentTypeRepository) {
    fun create(name: String) {
        if (existsWithSameName(name))
            throw InstrumentTypeAlreadyExistsError(name)

        InstrumentType.create(name).let {
            repository.save(it)
        }
    }

    private fun existsWithSameName(name: String) = InstrumentTypeCriteria.nameCriteria(name).let {
        repository.count(it) > 0
    }
}
