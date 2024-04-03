package org.eduardoleolim.organizadorPec660.core.agency.domain

import org.eduardoleolim.organizadorPec660.core.shared.domain.criteria.Criteria

interface AgencyRepository {
    fun matching(criteria: Criteria): List<Agency>

    fun count(criteria: Criteria): Int

    fun save(federalEntity: Agency)

    fun delete(federalEntityId: String)
}
