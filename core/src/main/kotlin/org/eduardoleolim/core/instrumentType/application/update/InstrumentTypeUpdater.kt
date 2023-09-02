package org.eduardoleolim.core.instrumentType.application.update

import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeCriteria
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeNotFoundError
import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeRepository

class InstrumentTypeUpdater(private val repository: InstrumentTypeRepository) {
    fun update(id: String, name: String) {
        val instrumentType = searchInstrumentType(id) ?: throw InstrumentTypeNotFoundError(id)

        instrumentType.apply {
            changeName(name)
            repository.save(this)
        }
    }

    private fun searchInstrumentType(id: String) = InstrumentTypeCriteria.idCriteria(id).let {
        repository.matching(it).firstOrNull()
    }
}
