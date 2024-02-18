package org.eduardoleolim.organizadorPec660.core.municipality.application.searchById

import org.eduardoleolim.organizadorPec660.core.shared.domain.bus.query.Query

class SearchMunicipalityByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
