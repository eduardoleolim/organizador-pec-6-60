package org.eduardoleolim.organizadorpec660.municipality.domain

import org.eduardoleolim.organizadorpec660.shared.domain.criteria.Criteria

interface MunicipalityRepository {
    fun matching(criteria: Criteria): List<Municipality>

    fun count(criteria: Criteria): Int

    fun save(municipality: Municipality)

    fun delete(municipalityId: String)
}
