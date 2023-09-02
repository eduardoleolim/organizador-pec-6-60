package org.eduardoleolim.core.instrumentType.application.search

import org.eduardoleolim.core.instrumentType.domain.InstrumentTypeRepository
import org.eduardoleolim.shared.domain.criteria.Criteria

class InstrumentTypeSearcher(private val repository: InstrumentTypeRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
