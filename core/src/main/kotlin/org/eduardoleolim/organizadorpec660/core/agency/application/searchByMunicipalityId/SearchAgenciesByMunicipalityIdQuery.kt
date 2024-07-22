package org.eduardoleolim.organizadorpec660.core.agency.application.searchByMunicipalityId

import org.eduardoleolim.organizadorpec660.core.shared.domain.bus.query.Query

class SearchAgenciesByMunicipalityIdQuery(municipalityId: String) : Query {
    private val municipalityId = municipalityId.trim()

    fun municipalityId(): String {
        return municipalityId
    }
}
