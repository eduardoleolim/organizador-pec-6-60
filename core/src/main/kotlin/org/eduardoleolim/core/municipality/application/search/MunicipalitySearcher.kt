package org.eduardoleolim.core.municipality.application.search

import org.eduardoleolim.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.shared.domain.criteria.Criteria

class MunicipalitySearcher(private val repository: MunicipalityRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
