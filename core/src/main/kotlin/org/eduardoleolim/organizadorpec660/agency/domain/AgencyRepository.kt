package org.eduardoleolim.organizadorpec660.agency.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface AgencyRepository {
    fun matching(criteria: Criteria): List<Agency>

    fun count(criteria: Criteria): Int

    fun save(agency: Agency)

    fun delete(agencyId: String)
}
