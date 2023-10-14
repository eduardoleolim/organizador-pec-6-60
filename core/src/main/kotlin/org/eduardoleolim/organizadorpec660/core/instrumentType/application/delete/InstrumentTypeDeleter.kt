package org.eduardoleolim.organizadorpec660.core.instrumentType.application.delete

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository

class InstrumentTypeDeleter(private val repository: InstrumentTypeRepository) {
    fun delete(id: String) {
        if (!exists(id))
            throw InstrumentTypeNotFoundError(id)

        repository.delete(id)
    }

    private fun exists(id: String) = InstrumentTypeCriteria.idCriteria(id).let {
        repository.count(it) > 0
    }
}
