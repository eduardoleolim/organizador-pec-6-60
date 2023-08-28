package org.eduardoleolim.core.municipality.application.searchById

import org.eduardoleolim.shared.domain.bus.query.Query

class SearchMunicipalityByIdQuery(id: String) : Query {
    private val id: String = id.trim()

    fun id(): String {
        return id
    }
}
