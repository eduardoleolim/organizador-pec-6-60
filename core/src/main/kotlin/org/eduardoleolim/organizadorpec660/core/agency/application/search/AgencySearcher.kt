package org.eduardoleolim.organizadorpec660.core.agency.application.search

import org.eduardoleolim.organizadorpec660.core.agency.domain.AgencyRepository
import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

class AgencySearcher(private val repository: AgencyRepository) {
    fun search(criteria: Criteria) = repository.matching(criteria)

    fun count(criteria: Criteria) = repository.count(criteria)
}
