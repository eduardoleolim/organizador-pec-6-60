package org.eduardoleolim.organizadorpec660.core.instrumentType.application.search

import org.eduardoleolim.organizadorpec660.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

class InstrumentTypeSearcher(private val repository: InstrumentTypeRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
