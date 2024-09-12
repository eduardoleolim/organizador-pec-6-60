package org.eduardoleolim.organizadorpec660.instrument.application.search

import org.eduardoleolim.organizadorpec660.instrument.domain.InstrumentRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

class InstrumentSearcher(private val repository: InstrumentRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)

    fun searchInstrumentFile(instrumentFileId: String) = repository.searchInstrumentFile(instrumentFileId)
}
