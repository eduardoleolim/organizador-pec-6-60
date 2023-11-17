package org.eduardoleolim.organizadorpec660.core.instrumentType.application.update

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository

class InstrumentTypeUpdater(private val repository: InstrumentTypeRepository) {
    fun update(id: String, name: String) {
        val instrumentType = searchInstrumentType(id) ?: throw InstrumentTypeNotFoundError(id)

        instrumentType.apply {
            changeName(name)
        }.let {
            repository.save(it)
        }
    }

    private fun searchInstrumentType(id: String) = InstrumentTypeCriteria.idCriteria(id).let {
        repository.matching(it).firstOrNull()
    }
}
