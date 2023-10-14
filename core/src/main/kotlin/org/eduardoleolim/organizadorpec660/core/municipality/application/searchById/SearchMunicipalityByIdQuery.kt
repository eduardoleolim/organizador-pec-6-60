package org.eduardoleolim.organizadorpec660.core.municipality.application.searchById

import org.eduardoleolim.organizadorpec660.shared.domain.bus.query.Query

class SearchMunicipalityByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
