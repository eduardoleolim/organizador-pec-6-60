package org.eduardoleolim.organizadorpec660.core.municipality.domain

import org.eduardoleolim.organizadorpec660.core.shared.domain.criteria.Criteria

interface MunicipalityRepository {
    fun matching(criteria: Criteria): List<Municipality>

    fun count(criteria: Criteria): Int

    fun save(municipality: Municipality)

    fun delete(municipalityId: String)
}
