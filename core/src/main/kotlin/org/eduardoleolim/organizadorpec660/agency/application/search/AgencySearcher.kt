package org.eduardoleolim.organizadorpec660.agency.application.search

import org.eduardoleolim.organizadorpec660.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

class AgencySearcher(private val repository: AgencyRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
