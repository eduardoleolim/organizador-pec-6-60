package org.eduardoleolim.organizadorpec660.core.agency.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

interface AgencyRepository {
    fun matching(criteria: Criteria): List<Agency>

    fun count(criteria: Criteria): Int

    fun save(agency: Agency)

    fun delete(agencyId: String)
}
