package org.eduardoleolim.organizadorPec660.core.municipality.application.search

import org.eduardoleolim.organizadorPec660.core.municipality.domain.MunicipalityRepository
import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

class MunicipalitySearcher(private val repository: MunicipalityRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
