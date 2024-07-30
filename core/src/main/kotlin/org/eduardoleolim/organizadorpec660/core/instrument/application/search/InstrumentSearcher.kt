package org.eduardoleolim.organizadorpec660.core.instrument.application.search

import org.eduardoleolim.organizadorpec660.core.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

class InstrumentSearcher(private val repository: InstrumentRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)

    fun searchInstrumentFile(instrumentFileId: String) = repository.searchInstrumentFile(instrumentFileId)
}
