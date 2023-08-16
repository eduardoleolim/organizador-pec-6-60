package org.eduardoleolim.core.municipality.domain

import org.eduardoleolim.shared.domain.criteria.Criteria

interface MunicipalityRepository {
    fun matching(criteria: Criteria): List<Municipality>

    fun count(criteria: Criteria): Int

    fun save(municipality: Municipality)

    fun delete(municipalityId: MunicipalityId)
}
