package org.eduardoleolim.organizadorPec660.core.instrumentType.application.create

import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentType
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeAlreadyExistsError
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorPec660.core.instrumentType.domain.InstrumentTypeRepository

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
